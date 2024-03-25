package com.ssafy.artchain.funding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundingCostCreateDto {
    private String mainVariety;

    private String subVariety;
}
