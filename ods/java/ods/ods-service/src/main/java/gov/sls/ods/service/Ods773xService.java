package gov.sls.ods.service;

import gov.sls.entity.ods.OdsUserFollowPackage;
import gov.sls.entity.ods.OdsUserPackageNotify;
import gov.sls.ods.dto.Ods773xIndividualDto;
import gov.sls.ods.repository.OdsIndividePackageSubRepository;
import gov.sls.ods.repository.OdsResourceIndividRepository;
import gov.sls.ods.repository.OdsUserPackageNotifyRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods773xService {
    @Autowired
    private OdsIndividePackageSubRepository odsIndividePackageSubRepository;
    
    @Autowired
    private OdsResourceIndividRepository odsResourceIndividRepository;

    @Autowired
    private OdsUserPackageNotifyRepository odsUserPackageNotifyRepository;
    
    /**
     * 取得需要通知的User list
     * 
     * @param odsCriteriaMap
     *            Map<String, List<Ods773CriteriaResourceDto>>
     *            key為odsCriteria.getResourceId()+"$$"+odsCriteria.getCriteriaId()
     * 
     * @return List<OdsUserFollowPackage> 訂閱未通知主題的使用者
     */
    public List<Ods773xIndividualDto> findNotifier() {
        // 取得 更新即通知 的 使用者
        List<Ods773xIndividualDto> notifier = odsIndividePackageSubRepository
                .findNotifier();

        return notifier;
    }

    public void updateNotifyMk(String userUnifyId, String mk) {
        
        odsResourceIndividRepository.updateNotifyMk(userUnifyId, mk);
    }
    
    public void createUserPackageNotify(List<Ods773xIndividualDto> notifier, Map<String, String> userideMail) {
        Date date = new Date();
        for (Ods773xIndividualDto notify : notifier) {
            if (null != userideMail.get(notify.getUserId())) {
                OdsUserPackageNotify oupn = new OdsUserPackageNotify();
                oupn.setUserId(notify.getUserId());
                oupn.setPackageId(notify.getPackageId());
                oupn.setUserRole(notify.getUserRole());
                oupn.setEmail(userideMail.get(notify.getUserId()));
                oupn.setResourceId(notify.getResourceId());
                oupn.setResourceCriteriaId(notify.getResourceCriteriaId());
                oupn.setCreated(date);
                oupn.setCreateUserId("SYS");
                odsUserPackageNotifyRepository.create(oupn);
            }
            log.info(notify.getUserId() + " role:" + notify.getUserRole());
        }
    }
    
}
