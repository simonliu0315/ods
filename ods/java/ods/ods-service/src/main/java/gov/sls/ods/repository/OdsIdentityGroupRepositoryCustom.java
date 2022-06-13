package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods704eGrid1Dto;

import java.util.List;


public interface OdsIdentityGroupRepositoryCustom {

    List<Ods704eGrid1Dto> findUnGroupPackageByNameSelPkg(String name, String selectedPkgList);  
    
    void createIdentityGroupByGupIdIdtIdList(String groupId, String chkIdentityIdList);
}
