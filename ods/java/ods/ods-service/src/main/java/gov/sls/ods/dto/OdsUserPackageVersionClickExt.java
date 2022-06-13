package gov.sls.ods.dto;

import gov.sls.entity.ods.OdsUserPackageVersionClick;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OdsUserPackageVersionClickExt extends OdsUserPackageVersionClick {

    /**
     * 
     */
    private static final long serialVersionUID = -8271832126225666164L;
    private BigDecimal count;
    private BigDecimal sum;
}
