package com.cdl.epms.dto.cycle;

import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CycleResponseDto {

    private Long id;
    private CycleType cycleType;
    private Integer year;
    private Quarter quarter;
    private LocalDate startDate;
    private LocalDate endDate;
    private CycleStatus status;
}
