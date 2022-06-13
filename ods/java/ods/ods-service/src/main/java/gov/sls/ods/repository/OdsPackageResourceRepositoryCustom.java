package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.ods.dto.Ods303eAnalysisDto;
import gov.sls.ods.dto.Ods703eTab2DialogDto;

import java.util.List;

import com.cht.commons.persistence.query.Query;

public interface OdsPackageResourceRepositoryCustom {
    
    List<Ods703eTab2DialogDto> findPackResByIdAndVer(String packageId, int packageVer);
    
    public void deletePackageResource(String packageId, int packageVer);
    
    public List<Ods303eAnalysisDto> findWorkbookByIdAndVer(String packageId, int packageVer);
    
    public void insertPackageResource(OdsPackageResource odsPackageResource);

    public List<OdsPackageResource> findPackageIdAndMaxPkgVer();
    
    public void deleteByPackageId(String packageId);
    
    public List<Ods303eAnalysisDto> findPackageByWorkbook(String workbookId, String workbookVer);
    
    List<Ods703eTab2DialogDto> findPackResInfoByIdAndVer(String packageId, int packageVer);

    public List<Ods703eTab2DialogDto> findPackResInfoByIdAndVerAndViewId(String packageId, int packageVer, String viewId);
}
