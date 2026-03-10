package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
import com.cdl.epms.common.enums.EmailerStatus;
import com.cdl.epms.dto.notifications.EmailerRequestDto;
import com.cdl.epms.dto.notifications.EmailerResponseDto;
import com.cdl.epms.dto.notifications.EmployeeDto;
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
import java.util.Arrays;

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

    private String getTemplateName(EmailTemplateType templateType) {

        switch (templateType) {

            case LAUNCH:
                return "launch-email-template";

            case REMINDER:
                return "reminder-email-template";

            case EXPIRY:
                return "expiry-email-template";

            default:
                throw new ValidationException("Invalid template type");
        }
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
            // Send emails to employees
            int emailsSent = sendEmailToEmployees(emailer, cycleType);

            // Update emailer status
            emailer.setStatus(EmailerStatus.ACTIVE);
            emailer.setActiveAt(LocalDateTime.now());
            emailerRepository.save(emailer);

            log.info("Successfully sent {} launch emails for cycle: {}", emailsSent, cycleType);
            return String.format("Launch email sent successfully to %d employees.", emailsSent);

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

        sendEmailToEmployees(emailer, cycleType);
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

        int emailsSent = sendEmailToEmployees(emailer, cycleType);

        return String.format("Email sent successfully to %d employees.", emailsSent);
    }

    private int sendEmailToEmployees(Emailer emailer, CycleType cycleType) {

        log.info("Fetching employees from API: {}", employeeApiUrl);

        EmployeeDto[] employees;
        try {
            employees = restTemplate.getForObject(employeeApiUrl, EmployeeDto[].class);
            log.info("Fetched {} employees from API", employees != null ? employees.length : 0);
        } catch (RestClientException e) {
            log.error("Failed to fetch employees from API: {}", employeeApiUrl, e);
            throw new RuntimeException("Failed to fetch employees list", e);
        }

        if (employees == null || employees.length == 0) {
            log.warn("No employees found to send emails");
            return 0;
        }

        String templateName = getTemplateName(emailer.getTemplateType());
        log.info("Using template: {}", templateName);

        int successCount = 0;
        int failureCount = 0;

        for (EmployeeDto employee : employees) {

            if (employee.getEmpEmailId() == null || employee.getEmpEmailId().isBlank()) {
                log.warn("Skipping employee {} - no email address", employee.getEmpName());
                continue;
            }

            try {
                Context context = new Context();
                context.setVariable("employeeName",
                        employee.getEmpName() != null ? employee.getEmpName() : "Employee");
                context.setVariable("subject", emailer.getSubject());
                context.setVariable("cycleType", cycleType.toString());
                context.setVariable("content", emailer.getContent().replace("\n", "<br>"));

                String htmlContent = templateEngine.process(templateName, context);

                sendHtmlEmail(employee.getEmpEmailId(), emailer.getSubject(), htmlContent);

                log.info("Email sent successfully to: {}", employee.getEmpEmailId());
                successCount++;

            } catch (Exception ex) {
                log.error("Failed to send email to: {}", employee.getEmpEmailId(), ex);
                failureCount++;
            }
        }

        log.info("Email sending completed - Success: {}, Failed: {}", successCount, failureCount);
        return successCount;
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