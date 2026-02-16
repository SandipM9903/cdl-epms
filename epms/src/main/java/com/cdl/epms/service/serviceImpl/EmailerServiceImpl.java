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
        emailer.setStatus(EmailerStatus.DRAFT);
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

        if (emailer.getStatus() == EmailerStatus.PUBLISHED) {
            throw new ConflictException("Published emailer cannot be edited");
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

        Emailer emailer = emailerRepository.findByCycleTypeAndStatus(cycleType, EmailerStatus.DRAFT)
                .orElseThrow(() -> new ResourceNotFoundException("No DRAFT emailer found for cycle type: " + cycleType));

        EmployeeDto[] employeesArray = restTemplate.getForObject(EMPLOYEE_API_URL, EmployeeDto[].class);

        if (employeesArray == null || employeesArray.length == 0) {
            throw new ResourceNotFoundException("No employees found from Employee Service");
        }

        List<EmployeeDto> employees = Arrays.asList(employeesArray);

        for (EmployeeDto employee : employees) {

            if (employee.getEmpEmailId() == null || employee.getEmpEmailId().trim().isEmpty()) {
                continue;
            }

            String employeeName = employee.getEmpName() != null ? employee.getEmpName() : "Employee";

            String formattedContent = emailer.getContent().replace("\n", "<br>");

            Context context = new Context();
            context.setVariable("employeeName", employeeName);
            context.setVariable("subject", emailer.getSubject());
            context.setVariable("cycleType", cycleType.toString());
            context.setVariable("content", formattedContent);

            String htmlTemplate = templateEngine.process("emailer-template", context);

            sendHtmlEmail(employee.getEmpEmailId(), emailer.getSubject(), htmlTemplate);
        }

        emailer.setStatus(EmailerStatus.PUBLISHED);
        emailer.setPublishedAt(LocalDateTime.now());
        emailer.setUpdatedAt(LocalDateTime.now());

        emailerRepository.save(emailer);

        return "Emailer published successfully and email sent to all employees";
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
}