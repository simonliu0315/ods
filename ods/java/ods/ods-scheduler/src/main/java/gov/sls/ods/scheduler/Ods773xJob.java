/**
 * 
 */
package gov.sls.ods.scheduler;

import gov.sls.commons.mail.SlsMailBuilder;
import gov.sls.commons.mail.SlsMailSender;
import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.OdsUserFollowPackage;
import gov.sls.ods.dto.Ods773xIndividualDto;
import gov.sls.ods.service.Ods773xService;
import gov.sls.ods.web.config.OdsSlsSacUaaEnvironments;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.sdp.login.SacUaaUserRegistry;

/**
 * 
 */
@Slf4j
@Service
public class Ods773xJob {

    @Autowired
    private Ods773xService service;

    @Autowired
    private SlsMailSender sender;
    
    @Autowired
    private SacUaaUserRegistry sacUaaUserRegistry;

    @Scheduled(cron = "0 0 0 * * ?") //每天零點零分
//    @Scheduled(fixedDelay=60000)
    public void run() { 
        log.info("Ods773xJob Start Executing!");
        
        // 取得需要通知的User list
        List<Ods773xIndividualDto> notifier = service.findNotifier();
        log.info("需要通知的User list " + notifier.size() + "筆。");

        // 組成通知email
        Map<String,String> emailContent = new HashMap<String,String>();
        Map<String,String> userUnifyIdMap = new HashMap<String,String>();
        for (Ods773xIndividualDto user : notifier) {
                String content = emailContent.get(user.getUserId());
                if (null == content) {
                    content = String.format("<html><head><style type='text/css'>.txt {font-family: '新細明體', Arial;font-size: 13px;color: #000;line-height: 18px;}</style></head><body><div align='center'><img src='https://sip.einvoice.nat.gov.tw/sip-main/file?type=epaper&file=epaper.gif' width='1000' height='115' /></div><br/><div align='center'>您好，您所訂閱的個人化主題 ");
                }
                content += "<br/>已經有新的內容發佈，";

                String url = "https://sip.einvoice.nat.gov.tw/ods-main/ODS311E/"+user.getPackageId()+"/1/02/"+user.getCode()+"/";
                content += "趕緊去看看吧！<a href='"+url+"'>請點我</a>。</div>";
                content += "<br/><div align='center' class='txt'>客服專線：0800-521-988　客服傳真：(04)2378-4468<br/>財政部電子發票智慧好生活平台版權所有　Copyrights c 2011 All Rights Reserved</div></body></html>";
                emailContent.put(user.getUserId(), content);
                userUnifyIdMap.put(user.getUserId(), user.getUserUnifyId());
                log.info("email content:" + emailContent.get(user.getUserId()));
        }

        log.info("需要通知的email " + emailContent.size() + "筆。");
        
        // query email by ids
        Set<String> userIds = new TreeSet<String>();  
        
        for (String userId : emailContent.keySet()) {
            log.info(" userId:{}", userId);
            userIds.add(userId);
        }
        log.info(" userIds size:{}", userIds.size());
        
        OdsSlsSacUaaEnvironments ossue = new OdsSlsSacUaaEnvironments();
        String dbKey = ossue.getFronKey();
        Map<String, String> userideMail =  this.sacUaaUserRegistry.findUserIdEMail(userIds, dbKey);
        log.info("dbKey:" + dbKey);
        log.info("userideMail size:" + userideMail.size());
        for (String userId : userideMail.keySet()) {
            log.info("userideMail " + userId + ":" + userideMail.get(userId));
        }

        // 發送通知 FIXME
        for (String userId : emailContent.keySet()) {
            String tmpContent = emailContent.get(userId);
            log.info("EMAIL " + userId + ":" + emailContent.get(userId));
            log.info("EMAILAddress " + userId + ":" + userideMail.get(userId));

            if (null != userideMail.get(userId)) {
                try {
                    SlsMailBuilder mail = new SlsMailBuilder();
                    mail.from("einvoice@fia.gov.tw", "電子發票智慧好生活網站")
                        .to(userideMail.get(userId), userId)
                        .subject("電子發票隨選服務更新通知")
                        .content(tmpContent,true);//指定內文為HTML格式
                    sender.send(mail.build());
                    // 紀錄已經通知
                    service.updateNotifyMk(userUnifyIdMap.get(userId), "Y");
                } catch (Exception e) {
                    // 紀錄通知失敗
                    service.updateNotifyMk(userUnifyIdMap.get(userId), "N");
                }
            } else {
                 // 紀錄通知失敗
                service.updateNotifyMk(userUnifyIdMap.get(userId), "N");
            }
        }
        // 寫入ODS_USER_PACKAGE_NOTIFY
        service.createUserPackageNotify(notifier, userideMail);
        
        log.info("Ods773xJob Finished");
    }

}
