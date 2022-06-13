package gov.sls.ods.dto;

import gov.sls.entity.ods.OdsResourceCriteria;
import lombok.Data;

@Data
public class OdsResourceCriteriaEtxDto {

    private OdsResourceCriteria odsResourceCriteria;
    private boolean isFollowUser;
}
