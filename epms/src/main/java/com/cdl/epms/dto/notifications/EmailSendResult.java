package com.cdl.epms.dto.notifications;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailSendResult {
    private int totalEmployees;
    private int totalManagers;
    private int employeesWithBothRoles;
    private int employeeEmailsSent;
    private int managerEmailsSent;
    private int totalEmailsSent;
    private int failedEmails;

    public String getSummary() {
        return String.format(
                "Sent %d emails to employees and %d emails to managers. Total: %d emails sent successfully. Failed: %d",
                employeeEmailsSent, managerEmailsSent, totalEmailsSent, failedEmails
        );
    }
}