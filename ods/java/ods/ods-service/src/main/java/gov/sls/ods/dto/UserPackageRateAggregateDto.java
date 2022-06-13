package gov.sls.ods.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserPackageRateAggregateDto {

    private BigDecimal rateAvg;
    private BigDecimal rateCount;
}
