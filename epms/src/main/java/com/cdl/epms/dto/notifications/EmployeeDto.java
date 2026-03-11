package com.cdl.epms.dto.notifications;

import lombok.Data;

@Data
public class EmployeeDto {
    private Long id;
    private Integer empId;
    private String firstName;
    private String lastName;
    private String emailId;
    private String reportingManager;
    private String reportingManagerEmailId;

    // Helper method to get full name
    public String getEmpName() {
        return (firstName != null ? firstName : "") +
                (lastName != null ? " " + lastName : "");
    }

    // Helper method to get email
    public String getEmpEmailId() {
        return emailId;
    }

    // Check if employee is a manager (has at least one reportee)
    public boolean isManager() {
        return reportingManagerEmailId != null && !reportingManagerEmailId.isBlank();
    }
}