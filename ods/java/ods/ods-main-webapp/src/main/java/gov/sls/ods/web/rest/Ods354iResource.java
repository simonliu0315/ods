package gov.sls.ods.web.rest;

import gov.sls.ods.dto.EPaper;
import gov.sls.ods.service.Ods354iService;
import gov.sls.ods.web.dto.Ods354iDto;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("ODS354I/rest")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS354I')")
public class Ods354iResource {
    
    @Autowired
    private Ods354iService service;

    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
    @RequestMapping(value = "/getEPaper/{preDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods354iDto getEPaper(@PathVariable String preDate) {
        log.debug("preDate:" + preDate);
        Ods354iDto dto = new Ods354iDto();
        List<EPaper> ePaperList = new ArrayList<EPaper>();
        try {
            ePaperList = service.getEPaper(preDate);
            if (ePaperList.isEmpty()) {
                dto.setCode("ODS01");
            } else {
                dto.setCode("ODS00");
            }
        } catch (Exception e) {
            dto.setCode("ODS99");
        }        
        dto.setEPaperList(ePaperList);
        
        return dto;
    }
}
