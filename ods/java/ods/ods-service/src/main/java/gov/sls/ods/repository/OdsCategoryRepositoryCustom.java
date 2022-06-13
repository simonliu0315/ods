package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsCategory;
import gov.sls.ods.dto.Ods702eDto;
import gov.sls.ods.dto.Ods702eGrid1Dto;
import gov.sls.ods.dto.Ods702eGrid2Dto;

import java.util.List;

public interface OdsCategoryRepositoryCustom {
    
    List<OdsCategory> findUnSelCategoryByResId(String resId);
    
    List<OdsCategory> findSelCategoryByResId(String resId);
    
    List<OdsCategory> findUnSelCategoryByCategoryId(String categoryId);
    
    List<OdsCategory> findSelCategoryByCategoryId(String categoryId);

    List<Ods702eGrid2Dto> findCategoryByName(String name, String description);
    
    List<Ods702eGrid1Dto> findResourceByCategoryId(String categoryId);
    
    List<Ods702eGrid1Dto> findUnCategoryResourceByNameSelRes(String name, String selectedResList);  
    
    String createCategoryByNameDescGrid1(Ods702eDto ods702eDto) throws Exception;  
    
    void updateCategoryByNameDescGrid1(Ods702eDto ods702eDto) throws Exception;  
    
    void deleteCategoryByCategoryId(Ods702eDto ods702eDto) throws Exception;  
    
}
