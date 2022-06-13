package gov.sls.ods.service;

import gov.sls.ods.dto.PortalStat;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods355iService {

    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    @Autowired
    private OdsPackageVersionRepository odsPackageVersionRepository;

    /**2019/11/11依據需求移除本統計
     * @return
     */
    public List<PortalStat> getWeeklyStat() {
        List<PortalStat> portalStatList = new ArrayList<PortalStat>();
        Date date = new Date();
        //主題數統計
        List<PortalStat> packageCount = odsPackageRepository.findPackageCount();
        if (!packageCount.isEmpty()) {
            //packageCount.get(0).setName("主題數");
            packageCount.get(0).setName("本週主題更新數");
            packageCount.get(0).setEndDate(date);
            packageCount.get(0).setUnit("個");
            portalStatList.addAll(packageCount);
        }                
        //本週主題更新數統計
        List<PortalStat> packageUpdateCount = odsPackageVersionRepository.findPackageUpdateCount();
        if (!packageUpdateCount.isEmpty()) {
            //packageUpdateCount.get(0).setName("本週主題更新數");
            packageUpdateCount.get(0).setName("本週主題版本更新數");
            packageUpdateCount.get(0).setEndDate(date);
            packageUpdateCount.get(0).setUnit("個");
            portalStatList.addAll(packageUpdateCount);
        }
        return portalStatList;
    }
    

    /**2019/11/11依據需求調整改用本統計
     * 累計主題發票數xx個 [至yyyy/mm/dd]
     * 本月主題版本更新數y個 [至yyyy/mm/dd] ，y則是因為每月都會有更新，所以當月更新後到月底，就可以一直看到y有數字
     * @return
     */
    public List<PortalStat> getStat() {
        List<PortalStat> portalStatList = new ArrayList<PortalStat>();
        Date date = new Date();
        //累計主題發票數xx個 [至yyyy/mm/dd]
        List<PortalStat> packageCount = odsPackageRepository.findAllPackageCount();
        if (!packageCount.isEmpty()) {
            //packageCount.get(0).setName("主題數");
            packageCount.get(0).setName("累計主題數");
            packageCount.get(0).setEndDate(date);
            packageCount.get(0).setUnit("個");
            portalStatList.addAll(packageCount);
        }                
        //本月主題版本更新數y個 [至yyyy/mm/dd]
        List<PortalStat> packageUpdateCount = odsPackageVersionRepository.findMonthPackageUpdateCount();
        if (!packageUpdateCount.isEmpty()) {
            packageUpdateCount.get(0).setName("本月主題版本更新數");
            packageUpdateCount.get(0).setEndDate(date);
            packageUpdateCount.get(0).setUnit("個");
            portalStatList.addAll(packageUpdateCount);
        }
        return portalStatList;
    }
    
    
    
}
