package com.cdl.epms.dto.cycle;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCycleRequestDto {

    private CycleType cycleType; // optional now

    @NotNull
    private Integer year;

    private Quarter quarter; // optional for annual

    private Integer reminderDays;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
