package gov.sls.ods.service;

import gov.sls.ods.dto.Ods702eDto;
import gov.sls.ods.dto.Ods702eGrid1Dto;
import gov.sls.ods.dto.Ods702eGrid2Dto;
import gov.sls.ods.repository.OdsCategoryRepository;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods702eService {

    
    @Autowired
    private OdsCategoryRepository odsCategoryRepository;
    
    public List<Ods702eGrid2Dto> findCategoryByName(String name, String description) {
        return odsCategoryRepository.findCategoryByName(name, description);
    }    
    
    public List<Ods702eGrid1Dto> findResourceByCategoryId(String categoryId) {
        return odsCategoryRepository.findResourceByCategoryId(categoryId);
    }    

    public List<Ods702eGrid1Dto> findUnCategoryResourceByNameSelRes(String name, String selectedResList) {
        return odsCategoryRepository.findUnCategoryResourceByNameSelRes(name, selectedResList);
    }  
    
    public String createCategoryByNameDescGrid1(Ods702eDto ods702eDto) throws Exception {
        return odsCategoryRepository.createCategoryByNameDescGrid1(ods702eDto);
    }  
    
    public void updateCategoryByNameDescGrid1(Ods702eDto ods702eDto) throws Exception {
        odsCategoryRepository.updateCategoryByNameDescGrid1(ods702eDto);
    } 
    
    public void deleteCategoryByCategoryId(Ods702eDto ods702eDto) throws Exception {
        odsCategoryRepository.deleteCategoryByCategoryId(ods702eDto);
    } 
    
}
