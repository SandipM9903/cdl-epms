package com.cdl.epms.dto.managerRating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerRatingRequestDTO {

    private Long goalId;
    private Integer managerRating;
    private String managerRemark;
}