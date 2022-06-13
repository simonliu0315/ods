package gov.sls.ods.dto;

import gov.sls.entity.ods.OdsPackageVersion;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class TemplateDynamicGeneratorDto {

    private String systemId;
    private BigDecimal showStars;
    private BigDecimal showScore; 
    
    private List<Ods303eAnalysisDto> analysisData;
    
    private String packageId;
    private int packageVer;
    
    public List<String[]> funcInfo;
    private List<OdsPackageVersion> publishVersions;
    public String parentBreadLink;
    public String breadLink;

}
