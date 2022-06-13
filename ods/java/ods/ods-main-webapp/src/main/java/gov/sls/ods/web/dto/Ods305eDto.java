package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsResourceCriteria;
import gov.sls.entity.ods.OdsUserFollowPackage;
import gov.sls.ods.dto.OdsResourceCriteriaEtxDto;

import java.util.List;

import lombok.Data;

@Data
public class Ods305eDto {

    private String packageId;
    private int packageVer;
    private List<OdsResourceCriteria> odsResourceCriteriaList;
    private List<OdsUserFollowPackage> odsUserFollowPackageList;
    private List<OdsResourceCriteriaEtxDto> odsResourceCriteriaEtxDtoList;
    
    private String[] criteriaSpecial;
    private String[] criteriaNormal;
    
}
