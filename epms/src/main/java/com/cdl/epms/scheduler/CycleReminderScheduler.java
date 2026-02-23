package com.cdl.epms.scheduler;

import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.repository.PerformanceCycleRepository;
import com.cdl.epms.service.services.EmailerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CycleReminderScheduler {

    private final PerformanceCycleRepository cycleRepository;
    private final EmailerService emailerService;

    @Scheduled(cron = "0 0 9 * * ?") // Every day at 9AM
    public void sendReminderEmails() {

        List<PerformanceCycle> activeCycles =
                cycleRepository.findByStatus(CycleStatus.ACTIVE)
                        .stream()
                        .toList();

        for (PerformanceCycle cycle : activeCycles) {

            if (cycle.getReminderDays() == null) continue;

            if (LocalDate.now().isAfter(cycle.getEndDate())) continue;

            if (cycle.getPublishedDate() == null) continue;

            long daysSincePublished =
                    java.time.temporal.ChronoUnit.DAYS.between(
                            cycle.getPublishedDate(),
                            LocalDate.now()
                    );

            if (daysSincePublished > 0 &&
                    daysSincePublished % cycle.getReminderDays() == 0) {
                emailerService.sendReminderEmail(cycle.getCycleType());
            }
        }
    }
}