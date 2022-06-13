package gov.sls.ods.dto;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.entity.ods.OdsPackageVersion;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class TemplateGeneratorDto {

    private List<List<PackageAndResourceDto>> mainData;
    private List<OdsPackageVersion> publishVersions;
    private BigDecimal showStars;
    private BigDecimal showScore;    
    private List<OdsPackageTag> packageTags;
    private List<PackageMetadataDto> packageMetadata;
    private List<OdsPackageVersion> publishPackageVerLast;
    private OdsPackageVersion publishPackage;
    private int datasetNums;
    private OdsPackage odsPackage;
         
}
