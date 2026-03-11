package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
import com.cdl.epms.common.enums.EmailerStatus;
import com.cdl.epms.dto.notifications.*;
import com.cdl.epms.exception.ConflictException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.Emailer;
import com.cdl.epms.repository.EmailerRepository;
import com.cdl.epms.service.services.EmailerService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailerServiceImpl implements EmailerService {

    private final EmailerRepository emailerRepository;
    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;

    @Value("${employee.api.url:http://localhost:9020/api/v1/employees}")
    private String employeeApiUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private EmailerResponseDto mapToDto(Emailer emailer) {
        return modelMapper.map(emailer, EmailerResponseDto.class);
    }

    private Emailer getEmailer(CycleType cycleType,
                               EmailTemplateType templateType,
                               EmailerStatus status) {

        return emailerRepository
                .findByCycleTypeAndTemplateTypeAndStatus(cycleType, templateType, status)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Email template not found for cycle=" + cycleType +
                                ", template=" + templateType +
                                ", status=" + status));
    }

    private String getTemplateName(EmailTemplateType templateType, boolean isManager) {
        String baseTemplate;

        switch (templateType) {
            case LAUNCH:
                baseTemplate = "launch-email-template";
                break;
            case REMINDER:
                baseTemplate = "reminder-email-template";
                break;
            case EXPIRY:
                baseTemplate = "expiry-email-template";
                break;
            default:
                throw new ValidationException("Invalid template type");
        }

        return isManager ? "manager-" + baseTemplate : baseTemplate;
    }

    @Override
    public EmailerResponseDto createEmailer(EmailerRequestDto dto) {

        if (dto == null) {
            throw new ValidationException("Request body is required");
        }

        if (dto.getCycleType() == null) {
            throw new ValidationException("Cycle type is required");
        }

        if (dto.getSubject() == null || dto.getSubject().isBlank()) {
            throw new ValidationException("Email subject is required");
        }

        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new ValidationException("Email content is required");
        }

        Emailer emailer = modelMapper.map(dto, Emailer.class);

        emailer.setStatus(EmailerStatus.NOT_STARTED);
        emailer.setCreatedAt(LocalDateTime.now());
        emailer.setUpdatedAt(LocalDateTime.now());

        return mapToDto(emailerRepository.save(emailer));
    }

    @Override
    public EmailerResponseDto editEmailer(Long id, EmailerRequestDto dto) {

        Emailer emailer = emailerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Emailer not found with id: " + id));

        if (emailer.getStatus() == EmailerStatus.ACTIVE) {
            throw new ConflictException("ACTIVE emailer cannot be edited");
        }

        if (dto.getSubject() != null && !dto.getSubject().isBlank()) {
            emailer.setSubject(dto.getSubject());
        }

        if (dto.getContent() != null && !dto.getContent().isBlank()) {
            emailer.setContent(dto.getContent());
        }

        emailer.setUpdatedAt(LocalDateTime.now());

        return mapToDto(emailerRepository.save(emailer));
    }

    @Override
    public EmailerResponseDto previewEmailer(Long id) {

        Emailer emailer = emailerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Emailer not found with id: " + id));

        return mapToDto(emailer);
    }

    @Override
    public String publishEmailer(CycleType cycleType) {

        log.info("Publishing email for cycle: {}", cycleType);

        Emailer activeEmailer = emailerRepository
                .findByCycleTypeAndTemplateTypeAndStatus(
                        cycleType,
                        EmailTemplateType.LAUNCH,
                        EmailerStatus.ACTIVE)
                .orElse(null);

        if (activeEmailer != null) {
            log.info("Launch email already sent for cycle: {}", cycleType);
            return "Launch email already sent for this cycle.";
        }

        Emailer emailer = getEmailer(
                cycleType,
                EmailTemplateType.LAUNCH,
                EmailerStatus.NOT_STARTED);

        try {
            // Fetch employees and categorize them
            List<EmployeeDto> allEmployees = fetchEmployees();
            List<EmployeeDto> managers = identifyManagers(allEmployees);
            List<EmployeeDto> nonManagers = identifyNonManagers(allEmployees, managers);

            log.info("Identified {} managers and {} non-managers out of {} total employees",
                    managers.size(), nonManagers.size(), allEmployees.size());

            // Send emails based on roles
            EmailSendResult result = sendRoleBasedEmails(EmailSendRequest.builder()
                    .cycleType(cycleType)
                    .templateType(EmailTemplateType.LAUNCH)
                    .employees(nonManagers)
                    .managers(managers)
                    .subject(emailer.getSubject())
                    .content(emailer.getContent())
                    .deadline(LocalDateTime.now().plusDays(30).toLocalDate()) // You can fetch this from cycle
                    .build());

            // Update emailer status
            emailer.setStatus(EmailerStatus.ACTIVE);
            emailer.setActiveAt(LocalDateTime.now());
            emailerRepository.save(emailer);

            log.info("Successfully sent launch emails for cycle: {}. Result: {}", cycleType, result.getSummary());
            return String.format("Launch emails sent successfully. %s", result.getSummary());

        } catch (Exception e) {
            log.error("Failed to send launch emails for cycle: {}", cycleType, e);
            throw new RuntimeException("Failed to send launch emails: " + e.getMessage());
        }
    }

    @Override
    public void sendReminderEmail(CycleType cycleType) {

        log.info("Sending reminder email for cycle: {}", cycleType);

        Emailer emailer = getEmailer(
                cycleType,
                EmailTemplateType.REMINDER,
                EmailerStatus.ACTIVE);

        // Fetch employees and send reminder emails
        List<EmployeeDto> allEmployees = fetchEmployees();
        List<EmployeeDto> managers = identifyManagers(allEmployees);
        List<EmployeeDto> nonManagers = identifyNonManagers(allEmployees, managers);

        sendRoleBasedEmails(EmailSendRequest.builder()
                .cycleType(cycleType)
                .templateType(EmailTemplateType.REMINDER)
                .employees(nonManagers)
                .managers(managers)
                .subject(emailer.getSubject())
                .content(emailer.getContent())
                .deadline(LocalDateTime.now().plusDays(7).toLocalDate())
                .pendingTeamMembers(calculatePendingTeamMembers(managers))
                .build());
    }

    @Override
    public EmailerResponseDto previewEmailerByType(CycleType cycleType,
                                                   EmailTemplateType templateType) {

        Emailer emailer = emailerRepository
                .findByCycleTypeAndTemplateTypeAndStatus(
                        cycleType,
                        templateType,
                        EmailerStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email template not found for cycle=" + cycleType +
                                        ", template=" + templateType));

        return mapToDto(emailer);
    }

    @Override
    public String sendEmailByTemplate(CycleType cycleType,
                                      EmailTemplateType templateType) {

        log.info("Sending email by template - Cycle: {}, Template: {}", cycleType, templateType);

        Emailer emailer = emailerRepository
                .findByCycleTypeAndTemplateTypeAndStatus(
                        cycleType,
                        templateType,
                        EmailerStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email template not found for cycle=" + cycleType +
                                        ", template=" + templateType));

        // Fetch employees and send emails
        List<EmployeeDto> allEmployees = fetchEmployees();
        List<EmployeeDto> managers = identifyManagers(allEmployees);
        List<EmployeeDto> nonManagers = identifyNonManagers(allEmployees, managers);

        EmailSendResult result = sendRoleBasedEmails(EmailSendRequest.builder()
                .cycleType(cycleType)
                .templateType(templateType)
                .employees(nonManagers)
                .managers(managers)
                .subject(emailer.getSubject())
                .content(emailer.getContent())
                .deadline(LocalDateTime.now().plusDays(7).toLocalDate())
                .build());

        return String.format("Email sent successfully. %s", result.getSummary());
    }

    @Override
    public EmailSendResult sendRoleBasedEmails(EmailSendRequest request) {

        log.info("Sending role-based emails - Type: {}, Employees: {}, Managers: {}",
                request.getTemplateType(),
                request.getEmployees() != null ? request.getEmployees().size() : 0,
                request.getManagers() != null ? request.getManagers().size() : 0);

        int employeeEmailsSent = 0;
        int managerEmailsSent = 0;
        int failedEmails = 0;

        // Find employees who are both employee and manager - FIXED: use collect instead of forEach
        Set<String> managerEmails = request.getManagers() != null ?
                request.getManagers().stream()
                        .map(EmployeeDto::getEmpEmailId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()) :
                new HashSet<>();

        // Send employee emails to non-managers
        if (request.getEmployees() != null) {
            for (EmployeeDto employee : request.getEmployees()) {
                if (employee.getEmpEmailId() == null || employee.getEmpEmailId().isBlank()) {
                    continue;
                }

                try {
                    // If this employee is also a manager, they'll get manager email separately
                    if (!managerEmails.contains(employee.getEmpEmailId())) {
                        sendSingleEmail(employee, request, false);
                        employeeEmailsSent++;
                    }
                } catch (Exception e) {
                    log.error("Failed to send employee email to: {}", employee.getEmpEmailId(), e);
                    failedEmails++;
                }
            }
        }

        // Send manager emails
        if (request.getManagers() != null) {
            for (EmployeeDto manager : request.getManagers()) {
                if (manager.getEmpEmailId() == null || manager.getEmpEmailId().isBlank()) {
                    continue;
                }

                try {
                    sendSingleEmail(manager, request, true);
                    managerEmailsSent++;
                } catch (Exception e) {
                    log.error("Failed to send manager email to: {}", manager.getEmpEmailId(), e);
                    failedEmails++;
                }
            }
        }

        int totalEmailsSent = employeeEmailsSent + managerEmailsSent;

        EmailSendResult result = EmailSendResult.builder()
                .totalEmployees(request.getEmployees() != null ? request.getEmployees().size() : 0)
                .totalManagers(request.getManagers() != null ? request.getManagers().size() : 0)
                .employeesWithBothRoles(managerEmails.size())
                .employeeEmailsSent(employeeEmailsSent)
                .managerEmailsSent(managerEmailsSent)
                .totalEmailsSent(totalEmailsSent)
                .failedEmails(failedEmails)
                .build();

        log.info("Role-based email sending completed: {}", result.getSummary());
        return result;
    }

    private void sendSingleEmail(EmployeeDto recipient, EmailSendRequest request, boolean isManager) {

        String templateName = getTemplateName(request.getTemplateType(), isManager);

        Context context = new Context();
        context.setVariable("employeeName", recipient.getEmpName() != null ? recipient.getEmpName() : "Employee");
        context.setVariable("subject", request.getSubject());
        context.setVariable("cycleType", request.getCycleType().toString());
        context.setVariable("content", request.getContent().replace("\n", "<br>"));

        if (request.getDeadline() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            context.setVariable("deadline", request.getDeadline().format(formatter));
        }

        if (isManager && request.getPendingTeamMembers() != null) {
            context.setVariable("pendingTeamMembers", request.getPendingTeamMembers());
        }

        String htmlContent = templateEngine.process(templateName, context);

        sendHtmlEmail(recipient.getEmpEmailId(), request.getSubject(), htmlContent);

        log.debug("Sent {} email to: {}", isManager ? "manager" : "employee", recipient.getEmpEmailId());
    }

    private List<EmployeeDto> fetchEmployees() {
        log.info("Fetching employees from API: {}", employeeApiUrl);

        try {
            EmployeeDto[] employees = restTemplate.getForObject(employeeApiUrl, EmployeeDto[].class);
            log.info("Fetched {} employees from API", employees != null ? employees.length : 0);

            if (employees == null) {
                return new ArrayList<>();
            }

            return Arrays.asList(employees);

        } catch (RestClientException e) {
            log.error("Failed to fetch employees from API: {}", employeeApiUrl, e);
            throw new RuntimeException("Failed to fetch employees list", e);
        }
    }

    private List<EmployeeDto> identifyManagers(List<EmployeeDto> allEmployees) {
        if (allEmployees == null || allEmployees.isEmpty()) {
            return new ArrayList<>();
        }

        // A manager is someone who has at least one reportee
        // We can identify this by checking if their email appears as reportingManagerEmailId for any employee
        Set<String> managerEmails = allEmployees.stream()
                .map(EmployeeDto::getReportingManagerEmailId)
                .filter(Objects::nonNull)
                .filter(email -> !email.isBlank())
                .collect(Collectors.toSet());

        return allEmployees.stream()
                .filter(emp -> managerEmails.contains(emp.getEmpEmailId()))
                .collect(Collectors.toList());
    }

    private List<EmployeeDto> identifyNonManagers(List<EmployeeDto> allEmployees, List<EmployeeDto> managers) {
        if (allEmployees == null || allEmployees.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> managerEmails = managers.stream()
                .map(EmployeeDto::getEmpEmailId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return allEmployees.stream()
                .filter(emp -> !managerEmails.contains(emp.getEmpEmailId()))
                .collect(Collectors.toList());
    }

    private Integer calculatePendingTeamMembers(List<EmployeeDto> managers) {
        // This is a placeholder - implement actual logic to count pending reviews
        // You might want to fetch this from a database
        return 5; // Default value
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.debug("Email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email to: " + toEmail, e);
        }
    }
}