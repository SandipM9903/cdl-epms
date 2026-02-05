package com.cdl.epms.dto.cycle;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCycleRequestDto {

    @NotNull
    private CycleType cycleType;

    @NotNull
    private Integer year;

    // Nullable for ANNUAL
    private Quarter quarter;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
