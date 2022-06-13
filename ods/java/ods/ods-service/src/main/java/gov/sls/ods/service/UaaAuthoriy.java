package gov.sls.ods.service;

import gov.sls.commons.core.security.login.impl.SlsSacUaaEnvironments;
import gov.sls.entity.ods.OdsPackage;

import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.commons.security.Authority;
import com.cht.sac.uaa.common.service.bean.UAAParam;
import com.cht.sac.uaa.m400.service.bean.UAA400Bean;
import com.cht.sac.uaa.m400.service.bean.UAA492Bean;
import com.cht.sac.uaa.sdp.login.SacUaaDataAuthority;

@Slf4j
@Service
public class UaaAuthoriy {
    
    @Autowired
    private SacUaaDataAuthority sacUaaDataAuthoriy; 
    
    @Autowired
    private SlsSacUaaEnvironments slsSacUaaEnvironments;
    
    public UAA400Bean queryDataRuleByTypeIdRuleId(String dataRuleId) {
        log.debug("dataRuleId:" + dataRuleId);
        String dataRuleType = "Topic";
        UAAParam uaaParam = this.slsSacUaaEnvironments.getFrontUAAParam();
        return sacUaaDataAuthoriy.queryDataRuleByTypeIdRuleId(dataRuleType, dataRuleId, uaaParam);
    }
    
    public void updateDataRuleByTypeIdRuleId(UAA400Bean uaa400Bean, String dataRuleName) {
        log.info("dataRuleName:" + dataRuleName);
        
        if (uaa400Bean != null && uaa400Bean.getDataRuleId() != null) { 
            log.info("dataRuleId:" + uaa400Bean.getDataRuleId());
            UAAParam uaaParam = this.slsSacUaaEnvironments.getFrontUAAParam();
            //修改名稱
            uaa400Bean.setDataRuleName(dataRuleName);
            sacUaaDataAuthoriy.updateDataRuleByTypeIdRuleId(uaa400Bean , uaaParam);
            log.info("UAA Update Finished!");
        }        
    }
    
    public List<UAA492Bean> uaaCheck(OdsPackage odsPackage) {
        boolean isForward = false;
        String fromDataRuleType = "TopicGroup";
        String fromDataRuleId = null;
        String toDataRuleType = "Topic"; 
        String toDataRuleId = odsPackage.getId();
        UAAParam uaaParam = this.slsSacUaaEnvironments.getFrontUAAParam();
        // 查出來的結果為資料權群組
        return sacUaaDataAuthoriy.findDataRuleRelation(isForward, fromDataRuleType, fromDataRuleId, toDataRuleType, toDataRuleId, uaaParam);
    }
    
    public int deleteDataRuleByTypeIdRuleId(String dataRuleId) {
        // 利用資料規則類型、資料權代碼刪除資料權
        log.debug("dataRuleId:" + dataRuleId);        
        String dataRuleType = "Topic";
        // deleteSize為刪除的筆數
        UAAParam uaaParam = this.slsSacUaaEnvironments.getFrontUAAParam();

        return sacUaaDataAuthoriy.deleteDataRuleByTypeIdRuleId(dataRuleType, dataRuleId, uaaParam);        
    }
    
    public Collection<? extends Authority> findDataAuthorityByUserId(String userId) {
        log.info("UserId:" + userId);
        UAAParam uaaParam = this.slsSacUaaEnvironments.getFrontUAAParam();
        return sacUaaDataAuthoriy.findDataAuthorityByUserId(userId, uaaParam);
    }

}
