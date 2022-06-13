package gov.sls.ods.service;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsResourceCriteria;
import gov.sls.ods.dto.Ods706eGrid3Dto;
import gov.sls.ods.dto.Ods706eGrid4Dto;
import gov.sls.ods.dto.Ods706eTab1FormBean;
import gov.sls.ods.dto.Ods706eTab2Dto;
import gov.sls.ods.dto.Ods706eTab2FormBean;
import gov.sls.ods.repository.OdsResourceCriteriaRepository;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods706eService {

    
    @Autowired
    private OdsResourceCriteriaRepository odsResourceCriteriaRepository;
    
   
    
    public List<Ods706eGrid3Dto> findCriteriasByResId(String resId, String name, String description) {
        /*log.debug("ENVIRONMENT_PUBLIC_PATH:"
                + propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH));*/
        return odsResourceCriteriaRepository.findCriteriasByResId(resId, name, description);
    }    
    
    public List<Ods706eGrid4Dto> findCriteriaDetailByResIdCriId(String resId, String criId) {
        return odsResourceCriteriaRepository.findCriteriaDetailByResIdCriId(resId, criId);
    }

    public void createCriteriaByResId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
        odsResourceCriteriaRepository.createCriteriaByResId(ods706eTab1FormBean);
    }
    
    public OdsResourceCriteria createCriteria(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
        //odsResourceCriteriaRepository.createCriteriaByResId(ods706eTab1FormBean);        
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
      //檢查名稱是否有重覆, 如果沒有才新增
        if (odsResourceCriteriaRepository.findByResourceIdAndName(ods706eTab1FormBean.getResId(), ods706eTab1FormBean.getName()).isEmpty()) {
            OdsResourceCriteria orc = new OdsResourceCriteria();
            orc.setResourceId(ods706eTab1FormBean.getResId());
            orc.setName(ods706eTab1FormBean.getName());
            orc.setDescription(ods706eTab1FormBean.getDescription());
            orc.setCreated(date);
            orc.setUpdated(date);
            orc.setCreateUserId(userId);
            orc.setUpdateUserId(userId);
            OdsResourceCriteria orcRtn = odsResourceCriteriaRepository.create(orc);
            return orcRtn;
        } else {
            return null;
        }
//        if (!ods706eTab2FormBeanList.isEmpty()) {
//            for (Ods706eTab2FormBean formbean : ods706eTab2FormBeanList) {
//                formbean.setResId(orc.getId().getResourceId());
//                formbean.setCriId(orcRtn.getId().getCriteriaId());                
//                odsResourceCriteriaRepository.createCriteriaDetailByResIdCriId(formbean);
//            }
//        }        
    }
    
    public void createDetail(OdsResourceCriteria odsResourceCriteria, 
            List<Ods706eTab2FormBean> ods706eTab2FormBeanList) throws Exception {        
        if (!ods706eTab2FormBeanList.isEmpty()) {
            for (Ods706eTab2FormBean formbean : ods706eTab2FormBeanList) {
                formbean.setResId(odsResourceCriteria.getResourceId());
                formbean.setCriId(odsResourceCriteria.getId());                
                odsResourceCriteriaRepository.createCriteriaDetailByResIdCriId(formbean);
            }
        }        
    }
    
    public void updateCriteriaByResIdCriId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
        odsResourceCriteriaRepository.updateCriteriaByResIdCriId(ods706eTab1FormBean);
    }
    
    public void updateCriteriaAndDetail(Ods706eTab1FormBean ods706eTab1FormBean, 
            List<Ods706eTab2FormBean> ods706eTab2FormBeanList) throws Exception{
        odsResourceCriteriaRepository.updateCriteriaByResIdCriId(ods706eTab1FormBean);
        
        //刪掉全部再新增
        odsResourceCriteriaRepository.deleteCriteriaDetailByResIdCriId(ods706eTab1FormBean);
        if (!ods706eTab2FormBeanList.isEmpty()) {
            for (Ods706eTab2FormBean formbean : ods706eTab2FormBeanList) {
                formbean.setResId(ods706eTab1FormBean.getResId());
                formbean.setCriId(ods706eTab1FormBean.getCriId()); 
                odsResourceCriteriaRepository.createCriteriaDetailByResIdCriId(formbean);
            }
        }
    }
    
    public void deleteCriteriaByResIdCriId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
        odsResourceCriteriaRepository.deleteCriteriaByResIdCriId(ods706eTab1FormBean);
    }
    
    public void deleteCriteriaAndDetail(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
        odsResourceCriteriaRepository.deleteCriteriaByResIdCriId(ods706eTab1FormBean);
        odsResourceCriteriaRepository.deleteCriteriaDetailByResIdCriId(ods706eTab1FormBean);
    }
    
    public List<Ods706eTab2Dto> findDatasetColsByResId(String resId) {
        return odsResourceCriteriaRepository.findDatasetColsByResId(resId);
    }
    
    public void createCriteriaDetailByResIdCriId(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception{
        odsResourceCriteriaRepository.createCriteriaDetailByResIdCriId(ods706eTab2FormBean);
    }
    
    public void updateCriteriaDetailByResIdCriIdCond(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception{
        odsResourceCriteriaRepository.updateCriteriaDetailByResIdCriIdCond(ods706eTab2FormBean);
    }
    
    public void deleteCriteriaDetailByResIdCriIdCond(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception{
        odsResourceCriteriaRepository.deleteCriteriaDetailByResIdCriIdCond(ods706eTab2FormBean);
    }
    
    
}
