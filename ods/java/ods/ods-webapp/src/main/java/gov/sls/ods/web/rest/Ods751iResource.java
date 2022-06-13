package gov.sls.ods.web.rest;

import gov.sls.ods.dto.PackageInfo;
import gov.sls.ods.service.Ods751iService;
import gov.sls.ods.web.dto.Ods751iDto;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("ODS751I/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS751I')")
public class Ods751iResource {
    
    @Autowired
    private Ods751iService service;

    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
    @RequestMapping(value = "/getPackageInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods751iDto getPackageInfo() {
        Ods751iDto dto = new Ods751iDto();
        List<PackageInfo> packageInfoList = new ArrayList<PackageInfo>();
        try {
            packageInfoList = service.getPackageInfo();
            if (packageInfoList.isEmpty()) {
                dto.setCode("ODS01");
            } else {
                dto.setCode("ODS00");
            }
        } catch (Exception e) {
            dto.setCode("ODS99");
        }        
        dto.setPackageInfoList(packageInfoList);
        
        return dto;
    }
}
