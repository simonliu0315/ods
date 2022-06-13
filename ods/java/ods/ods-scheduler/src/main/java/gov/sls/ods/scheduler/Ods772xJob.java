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
import gov.sls.ods.dto.Ods772CriteriaResourceDto;
import gov.sls.ods.service.Ods772xService;
import gov.sls.ods.web.config.OdsSlsSacUaaEnvironments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.sdp.login.SacUaaUserRegistry;

/**
 * 
 */
@Slf4j
@Service
public class Ods772xJob {

    @Autowired
    private Ods772xService service;

    @Autowired
    private SlsMailSender sender;
    
    @Autowired
    private SacUaaUserRegistry sacUaaUserRegistry;

    @Scheduled(cron = "0 0 0 * * ?") //每天零點零分
//    @Scheduled(fixedDelay=60000)
    public void run() { 
        log.info("Ods772xJob Start Executing!");

        // 1.取得尚未通知的主題
        List<OdsPackageResource> nowPkgList = service.findNotyetNotifierPackage();
        if (CollectionUtils.isEmpty(nowPkgList)) {
            log.info("Ods772xJob End Executing! 無未通知主題。");
            return;
        }
        log.info("取得尚未通知的主題 " + nowPkgList.size() + "筆。");
        List<String> resourceIdList = new ArrayList<String>();
        List<String> packageIdList = new ArrayList<String>();
        Map<String, OdsPackageResource> packageResource = new HashMap<String, OdsPackageResource>();
        for (OdsPackageResource odsPackageResource : nowPkgList) {
            resourceIdList.add(odsPackageResource.getId().getResourceId());
            packageResource.put(odsPackageResource.getId().getPackageId(), odsPackageResource);
        }
        packageIdList.addAll(packageResource.keySet());

        // 製作odsResourceMap 給予後續製作通知內容email使用key為ResourceId
        Map<String, OdsResource> odsResourceMap = service.findOdsResourceMap(resourceIdList);
        log.info("odsResourceMap " + odsResourceMap.size() + "筆。");

        // 製作odsPackageMap 給予後續製作通知內容email使用key為packageId
        Map<String, OdsPackage> odsPackageMap = service.findOdsPackageMap(packageIdList);
        log.info("odsPackageMap " + odsPackageMap.size() + "筆。");

        // 找出有更新的主題之門檻List
        // key為odsCriteria.getResourceId()+"$$"+odsCriteria.getCriteriaId()
        Map<String, List<Ods772CriteriaResourceDto>> odsCriteriaMap = service
                .findOdsCriteriaMap(resourceIdList);
        log.info("有更新的主題之門檻List " + odsCriteriaMap.size() + "筆。");

        for(String key : odsCriteriaMap.keySet()){
            log.info("odsCriteriaMap:{}, {}", key, odsCriteriaMap.get(key));
        }
        
        // 取得需要通知的User list
        List<OdsUserFollowPackage> updateFollowUser = service.findNotifier(odsCriteriaMap);
        log.info("需要通知的User list " + updateFollowUser.size() + "筆。");

        // 組成通知email
        Map<String,String> emailContent = new HashMap<String,String>();
        List<OdsUserFollowPackage> notifyList = new ArrayList<OdsUserFollowPackage>(); //要寫入ODS_USER_PACKAGE_NOTIFY的List
        for (OdsUserFollowPackage user : updateFollowUser) {
            OdsPackage odsPackage = odsPackageMap.get(user.getPackageId());
            OdsResource odsResource = odsResourceMap.get(user.getResourceId());
            log.info("EMAIL GEN packageId:" + user.getPackageId() + " ::" + odsPackage);
            if (null != odsPackage) {// 有訂閱到該packageId才行

                // key為odsCriteria.getResourceId()+"$$"+odsCriteria.getCriteriaId()
                String resourceCriteriaKey = user.getResourceId() + "$$" + user.getResourceCriteriaId();
                List<Ods772CriteriaResourceDto> crilist = odsCriteriaMap.get(resourceCriteriaKey);

                String userNm = user.getUserId();// FIXME 使用者名稱,暫時先用user id
                String packageNm = odsPackage.getName();// 主題名稱
                String resourceNm = "";// 案材及案例名稱
                int packageVer = 0;// 主題版本
                OdsPackageResource odsPackageResource = null;
                if (null != (odsPackageResource = packageResource.get(odsPackage.getId()))) {
                    packageVer = odsPackageResource.getPackageVer();
                }

                String content = emailContent.get(user.getUserId());
                if (null == content) {
                    content = String.format("<html><head><style type='text/css'>.txt {font-family: '新細明體', Arial;font-size: 13px;color: #000;line-height: 18px;}</style></head><body><div align='center'><img src='https://sip.einvoice.nat.gov.tw/sip-main/file?type=epaper&file=epaper.gif' width='1000' height='115' /></div><br/>%1$s您好，您所訂閱的主題 ", "");
                }
                content += String.format("<br/>%1$s，已發佈新版本 %2$d。", packageNm, packageVer);

                if (null != odsPackage && null != odsResource) {// 更新即通知resourceId為null
                    log.info("EMAIL GEN ResourceId:" + user.getResourceId() + " ::" + odsResource);
                    resourceNm = odsResource.getName();// 案材及案例名稱
                    content += String.format("<br/>且內容的素材及案例 %1$s 資料", resourceNm);
                }
                log.info("OdsUserFollowPackage userNm:" + userNm + " packageNm:" + packageNm
                        + " packageVer:" + packageVer + " resourceNm:" + resourceNm
                        + " resourceCriteriaKey:" + resourceCriteriaKey);
                if (null != crilist) {
                    log.info("OdsUserFollowPackage crilist:" + crilist.size());
                    content += "，符合您先前所勾選之訂閱方案";
                    for (Ods772CriteriaResourceDto odsResourceCriteria : crilist) {
                        content += (" "+odsResourceCriteria.getName());// 方案名稱
                    }
                }
                String url = "https://sip.einvoice.nat.gov.tw/ods-main/ODS303E/"+user.getPackageId()+"/"+packageVer+"/";
                content.replace(" 。趕緊去看看吧！<a href='"+url+"'>請點我</a>。</body></html>", "");
                content += " 。趕緊去看看吧！<a href='"+url+"'>請點我</a>。</body></html>";
                emailContent.put(user.getUserId(), content);
                notifyList.add(user);
            }
        }
        //加入footer
//        for (OdsUserFollowPackage user : updateFollowUser) {
//            String content = emailContent.get(user.getUserId());
//            if (null != content) {
//                content += "<br/><div align='center' class='txt'>客服專線：0800-521-988　客服傳真：(04)2378-4468<br/>財政部電子發票智慧好生活平台版權所有　Copyrights c 2011 All Rights Reserved</div>";
//                emailContent.put(user.getUserId(), content);
//                log.info("email content:" + emailContent.get(user.getUserId()));
//            }
//        }
        
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
            
            if(tmpContent != null){
                tmpContent += "<br/><br/><div align='center' class='txt' style=\"background-color:#E9F5FE\">客服專線：0800-521-988　客服傳真：(04)2378-4468<br/>財政部電子發票智慧好生活平台版權所有　Copyrights c 2011 All Rights Reserved</div>";
            }
            if (null != userideMail.get(userId)) {
                SlsMailBuilder mail = new SlsMailBuilder();
                mail.from("einvoice@fia.gov.tw", "電子發票智慧好生活網站")
                    .to(userideMail.get(userId), userId)
                    .subject("電子發票隨選服務更新通知")
                    .content(tmpContent,true);//指定內文為HTML格式
                sender.send(mail.build());
            }
        }
        
        // 寫入ODS_USER_PACKAGE_NOTIFY
        service.createUserPackageNotify(notifyList, userideMail);

        // 紀錄已經通知
        service.noticePackageVersion(packageResource);
        log.info("Ods772xJob Finished");
    }

}
