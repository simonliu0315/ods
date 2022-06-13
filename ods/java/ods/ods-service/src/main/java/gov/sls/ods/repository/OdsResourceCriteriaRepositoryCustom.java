package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods706eGrid3Dto;
import gov.sls.ods.dto.Ods706eGrid4Dto;
import gov.sls.ods.dto.Ods706eTab1FormBean;
import gov.sls.ods.dto.Ods706eTab2Dto;
import gov.sls.ods.dto.Ods706eTab2FormBean;

import java.util.List;



public interface OdsResourceCriteriaRepositoryCustom {
 
    public List<Ods706eGrid3Dto> findCriteriasByResId(String resId, String name, String description);
    
    public List<Ods706eGrid4Dto> findCriteriaDetailByResIdCriId(String resId, String criId);
    
    public void createCriteriaByResId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception;
    
    public void updateCriteriaByResIdCriId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception;
    
    public void deleteCriteriaByResIdCriId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception;
    
    public List<Ods706eTab2Dto> findDatasetColsByResId(String resId);
    
    public void createCriteriaDetailByResIdCriId(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception;
    
    public void updateCriteriaDetailByResIdCriIdCond(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception;
    
    public void deleteCriteriaDetailByResIdCriIdCond(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception;
    
    public void deleteCriteriaDetailByResIdCriId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception;
}
