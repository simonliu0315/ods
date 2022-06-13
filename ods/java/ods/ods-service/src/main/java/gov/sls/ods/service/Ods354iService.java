package gov.sls.ods.service;

import gov.sls.ods.dto.EPaper;
import gov.sls.ods.repository.OdsPackageRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Slf4j
@Service
public class Ods354iService {

    @Autowired
    private OdsPackageRepository odsPackageRepository;

    public List<EPaper> getEPaper(String preDateStr) throws ParseException {
        
        
        //設定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //進行轉換
        //Date preDate = sdf.parse(preDateStr + "235959");
        Date preDate = sdf.parse(preDateStr);
        List<EPaper> ePaperList = odsPackageRepository.findEPaper(preDate);
        if (!ePaperList.isEmpty()) {
            for (EPaper ep : ePaperList) {
                if (!Strings.isNullOrEmpty(ep.getImageUrl())) {
                    ep.setImageUrl("/ods-main/ODS308E/public/package/" + ep.getPackageId() + "/image/" + ep.getImageUrl());
                }
                if("02".equals(ep.getType())){
                    ep.setLink("/ods-main/ODS311E/" + ep.getPackageId()+"/1/02/"+ep.getCode()+"/"); //項目連結URL
                } else {
                    ep.setLink("/ods-main/ODS303E/" + ep.getPackageId() + "/" + ep.getPackageVer() + "/"); //項目連結URL	
                }
                
            }
        }
        
        return ePaperList;
    }
}
