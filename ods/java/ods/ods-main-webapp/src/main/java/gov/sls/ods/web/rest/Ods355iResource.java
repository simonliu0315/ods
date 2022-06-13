package gov.sls.ods.web.rest;

import gov.sls.ods.dto.PortalStat;
import gov.sls.ods.service.Ods355iService;
import gov.sls.ods.web.dto.Ods355iDto;

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
@RequestMapping("ODS355I/rest")
public class Ods355iResource {
    
    @Autowired
    private Ods355iService service;

    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
    @RequestMapping(value = "/getStat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods355iDto getStat() {
        Ods355iDto dto = new Ods355iDto();
        List<PortalStat> portalStatList = new ArrayList<PortalStat>();
        try {
            portalStatList = service.getStat();
            dto.setCode("ODS00");
        } catch (Exception e) {
            dto.setCode("ODS99");
        }        
        dto.setPortalStatList(portalStatList);
        
        return dto;
    }

}
