package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CycleType;
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
import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailerServiceImpl implements EmailerService {

    private final EmailerRepository emailerRepository;
    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;

    private static final String EMPLOYEE_API_URL = "http://localhost:9020/api/v1/employees";
    private static final String FROM_EMAIL = "m.harikascm@gmail.com";

    private EmailerResponseDto mapToDto(Emailer emailer) {
        return modelMapper.map(emailer, EmailerResponseDto.class);
    }

    @Override
    public EmailerResponseDto createEmailer(EmailerRequestDto dto) {

        if (dto == null) {
            throw new ValidationException("Request body is required");
        }

        if (dto.getCycleType() == null) {
            throw new ValidationException("Cycle type is required");
        }

        if (dto.getSubject() == null || dto.getSubject().trim().isEmpty()) {
            throw new ValidationException("Email subject is required");
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ValidationException("Email content is required");
        }

        Emailer emailer = modelMapper.map(dto, Emailer.class);
        emailer.setStatus(EmailerStatus.NOT_STARTED);
        emailer.setCreatedAt(LocalDateTime.now());
        emailer.setUpdatedAt(LocalDateTime.now());

        Emailer saved = emailerRepository.save(emailer);

        return mapToDto(saved);
    }

    @Override
    public EmailerResponseDto editEmailer(Long id, EmailerRequestDto dto) {

        if (id == null) {
            throw new ValidationException("Emailer ID is required");
        }

        if (dto == null) {
            throw new ValidationException("Request body is required");
        }

        Emailer emailer = emailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emailer not found with id: " + id));

        if (emailer.getStatus() == EmailerStatus.ACTIVE) {
            throw new ConflictException("ACTIVE emailer cannot be edited");
        }

        if (dto.getSubject() != null && !dto.getSubject().trim().isEmpty()) {
            emailer.setSubject(dto.getSubject());
        }

        if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
            emailer.setContent(dto.getContent());
        }

        if (dto.getCycleType() != null) {
            emailer.setCycleType(dto.getCycleType());
        }

        emailer.setUpdatedAt(LocalDateTime.now());

        Emailer updated = emailerRepository.save(emailer);

        return mapToDto(updated);
    }

    @Override
    public EmailerResponseDto previewEmailer(Long id) {

        if (id == null) {
            throw new ValidationException("Emailer ID is required");
        }

        Emailer emailer = emailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emailer not found with id: " + id));

        return mapToDto(emailer);
    }

    @Override
    public String publishEmailer(CycleType cycleType) {

        if (cycleType == null) {
            throw new ValidationException("Cycle type is required");
        }

        // 🔹 If already ACTIVE → don't resend
        Emailer activeEmailer = emailerRepository
                .findByCycleTypeAndStatus(cycleType, EmailerStatus.ACTIVE)
                .orElse(null);

        if (activeEmailer != null) {
            return "Email already sent for this cycle.";
        }

        // 🔹 Get existing NOT_STARTED or create new
        Emailer emailer = emailerRepository
                .findByCycleTypeAndStatus(cycleType, EmailerStatus.NOT_STARTED)
                .orElseGet(() -> {
                    Emailer e = new Emailer();
                    e.setCycleType(cycleType);
                    e.setSubject("Performance Cycle " + cycleType + " Started");
                    e.setContent("The " + cycleType +
                            " performance cycle is now started. Please complete before expiry.");
                    e.setStatus(EmailerStatus.NOT_STARTED);
                    return emailerRepository.save(e);
                });

        EmployeeDto[] employeesArray =
                restTemplate.getForObject(EMPLOYEE_API_URL, EmployeeDto[].class);

        if (employeesArray == null || employeesArray.length == 0) {
            return "No employees found.";
        }

        List<EmployeeDto> employees = Arrays.asList(employeesArray);

        for (EmployeeDto employee : employees) {

            if (employee.getEmpEmailId() == null ||
                    employee.getEmpEmailId().trim().isEmpty()) {
                continue;
            }

            try {
                String employeeName = employee.getEmpName() != null
                        ? employee.getEmpName()
                        : "Employee";

                Context context = new Context();
                context.setVariable("employeeName", employeeName);
                context.setVariable("subject", emailer.getSubject());
                context.setVariable("cycleType", cycleType.toString());
                context.setVariable("content",
                        emailer.getContent().replace("\n", "<br>"));

                String htmlTemplate =
                        templateEngine.process("emailer-template", context);

                sendHtmlEmail(
                        employee.getEmpEmailId(),
                        emailer.getSubject(),
                        htmlTemplate
                );

            } catch (Exception ex) {
                System.err.println("Failed to send to: "
                        + employee.getEmpEmailId());
            }
        }

        // 🔹 Mark as ACTIVE only once
        emailer.setStatus(EmailerStatus.ACTIVE);
        emailer.setActiveAt(LocalDateTime.now());
        emailerRepository.save(emailer);

        return "Launch email sent successfully.";
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(FROM_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to: " + toEmail);
        }
    }

    @Override
    public void sendReminderEmail(CycleType cycleType) {

        if (cycleType == null) {
            throw new ValidationException("Cycle type is required");
        }

        Emailer emailer = emailerRepository
                .findByCycleTypeAndStatus(cycleType, EmailerStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No ACTIVE emailer found for cycle type: " + cycleType
                        )
                );

        EmployeeDto[] employeesArray =
                restTemplate.getForObject(EMPLOYEE_API_URL, EmployeeDto[].class);

        if (employeesArray == null || employeesArray.length == 0) {
            return; // No employees, no reminder
        }

        List<EmployeeDto> employees = Arrays.asList(employeesArray);

        for (EmployeeDto employee : employees) {

            if (employee.getEmpEmailId() == null ||
                    employee.getEmpEmailId().trim().isEmpty()) {
                continue;
            }

            String employeeName =
                    employee.getEmpName() != null
                            ? employee.getEmpName()
                            : "Employee";

            String reminderContent =
                    emailer.getContent() +
                            "\n\nReminder: This performance cycle is still ACTIVE. Please complete before expiry.";

            Context context = new Context();
            context.setVariable("employeeName", employeeName);
            context.setVariable("subject", "Reminder - " + emailer.getSubject());
            context.setVariable("cycleType", cycleType.toString());
            context.setVariable("content", reminderContent.replace("\n", "<br>"));

            String htmlTemplate =
                    templateEngine.process("emailer-template", context);

            sendHtmlEmail(
                    employee.getEmpEmailId(),
                    "Reminder - " + emailer.getSubject(),
                    htmlTemplate
            );
        }
    }
}