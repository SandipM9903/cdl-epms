package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailerStatus;
import com.cdl.epms.dto.notifications.EmailerRequestDto;
import com.cdl.epms.dto.notifications.EmailerResponseDto;
import com.cdl.epms.dto.notifications.EmployeeDto;
import com.cdl.epms.exception.BusinessException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.model.Emailer;
import com.cdl.epms.repository.EmailerRepository;
import com.cdl.epms.service.services.EmailerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailerServiceImpl implements EmailerService {

    private final EmailerRepository emailerRepository;
    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.host}")
    private String mailHost;

    private static final String EMPLOYEE_API_URL = "http://localhost:9020/api/v1/employees";

    public EmailerServiceImpl(EmailerRepository emailerRepository,
                              RestTemplate restTemplate,
                              JavaMailSender mailSender) {
        this.emailerRepository = emailerRepository;
        this.restTemplate = restTemplate;
        this.mailSender = mailSender;
    }

    private EmailerResponseDto mapToDto(Emailer emailer) {

        EmailerResponseDto dto = new EmailerResponseDto();

        dto.setId(emailer.getId());
        dto.setCycleType(emailer.getCycleType());
        dto.setSubject(emailer.getSubject());
        dto.setContent(emailer.getContent());
        dto.setStatus(emailer.getStatus());
        dto.setCreatedAt(emailer.getCreatedAt());
        dto.setUpdatedAt(emailer.getUpdatedAt());
        dto.setPublishedAt(emailer.getPublishedAt());

        return dto;
    }

    @Override
    public EmailerResponseDto createEmailer(EmailerRequestDto dto) {

        if (dto.getCycleType() == null) {
            throw new BusinessException("Cycle type is required");
        }

        if (dto.getSubject() == null || dto.getSubject().trim().isEmpty()) {
            throw new BusinessException("Email subject is required");
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new BusinessException("Email content is required");
        }

        Emailer emailer = new Emailer();
        emailer.setCycleType(dto.getCycleType());
        emailer.setSubject(dto.getSubject());
        emailer.setContent(dto.getContent());
        emailer.setStatus(EmailerStatus.DRAFT);

        return mapToDto(emailerRepository.save(emailer));
    }

    @Override
    public EmailerResponseDto editEmailer(Long id, EmailerRequestDto dto) {

        Emailer emailer = emailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emailer not found with id: " + id));

        if (emailer.getStatus() == EmailerStatus.PUBLISHED) {
            throw new BusinessException("Published emailer cannot be edited");
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

        return mapToDto(emailerRepository.save(emailer));
    }

    @Override
    public EmailerResponseDto previewEmailer(Long id) {

        Emailer emailer = emailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emailer not found with id: " + id));

        return mapToDto(emailer);
    }

    @Override
    public String publishEmailer(CycleType cycleType) {

        Emailer emailer = emailerRepository.findByCycleTypeAndStatus(cycleType, EmailerStatus.DRAFT)
                .orElseThrow(() -> new BusinessException("No DRAFT emailer found for cycle type: " + cycleType));

        // 🔥 Call Employee Service API
        EmployeeDto[] employeesArray = restTemplate.getForObject(EMPLOYEE_API_URL, EmployeeDto[].class);

        if (employeesArray == null || employeesArray.length == 0) {
            throw new BusinessException("No employees found from Employee Service");
        }

        List<EmployeeDto> employees = Arrays.asList(employeesArray);

        // 🔥 Send Email to all employees
        for (EmployeeDto employee : employees) {

            if (employee.getEmpEmailId() == null || employee.getEmpEmailId().trim().isEmpty()) {
                continue;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("m.harikascm@gmail.com");
            message.setTo(employee.getEmpEmailId());
            message.setSubject(emailer.getSubject());
            message.setText(emailer.getContent());

            mailSender.send(message);
        }

        // Update status
        emailer.setStatus(EmailerStatus.PUBLISHED);
        emailer.setPublishedAt(LocalDateTime.now());

        emailerRepository.save(emailer);

        return "Emailer published successfully and email sent to all employees";
    }
}