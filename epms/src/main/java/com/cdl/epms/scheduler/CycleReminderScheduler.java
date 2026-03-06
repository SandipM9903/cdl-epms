package com.cdl.epms.scheduler;

import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.repository.PerformanceCycleRepository;
import com.cdl.epms.service.services.EmailerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CycleReminderScheduler {

    private final PerformanceCycleRepository cycleRepository;
    private final EmailerService emailerService;

    // Every day at 9AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendReminderEmails() {

        LocalDate today = LocalDate.now();

        System.out.println("Scheduler running: " + today);

        List<PerformanceCycle> cycles =
                cycleRepository.findAllByStatus(CycleStatus.ACTIVE);

        for (PerformanceCycle cycle : cycles) {

            if (cycle.getPublishedDate() == null ||
                    cycle.getEndDate() == null ||
                    cycle.getReminderDays() == null) {
                continue;
            }

            long daysSincePublished =
                    ChronoUnit.DAYS.between(cycle.getPublishedDate(), today);

            long daysUntilExpiry =
                    ChronoUnit.DAYS.between(today, cycle.getEndDate());

            // -------- FINAL REMINDER (Priority) --------
            if (daysUntilExpiry == 1) {

                System.out.println("Final reminder for cycle: " + cycle.getId());
                emailerService.sendReminderEmail(cycle.getCycleType());
                continue;
            }

            // -------- PERIODIC REMINDER --------
            if (daysSincePublished > 0 &&
                    daysSincePublished % cycle.getReminderDays() == 0 &&
                    today.isBefore(cycle.getEndDate())) {

                System.out.println("Periodic reminder for cycle: " + cycle.getId());
                emailerService.sendReminderEmail(cycle.getCycleType());
            }

            // -------- AUTO CLOSE --------
            if (!today.isBefore(cycle.getEndDate())) {

                cycle.setStatus(CycleStatus.CLOSED);
                cycleRepository.save(cycle);

                System.out.println("Cycle auto closed: " + cycle.getId());
            }
        }
    }
}