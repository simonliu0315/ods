package gov.sls.ods.web.rest;

import gov.sls.ods.Messages;
import gov.sls.ods.dto.Ods707eGridDto;
import gov.sls.ods.service.Ods707eService;
import gov.sls.ods.web.dto.Ods707eDto;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS707E/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS707E')")
public class Ods707eResource {
    
    @Autowired
    private Ods707eService service;

    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
    @RequestMapping(value = "/find/all", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods707eDto find(@RequestBody Ods707eDto ods707eDto, Alerter alerter) {
        log.debug("name:"+ods707eDto.getPackageName());
        log.debug("desc:"+ods707eDto.getPackageDescription());
        log.debug("sdate:"+ods707eDto.getStartDate());
        log.debug("edate:"+ods707eDto.getEndDate());
        List<Ods707eGridDto> ods707eGridDtoList = service.findGridData(ods707eDto.getPackageName(), ods707eDto.getPackageDescription(), 
                ods707eDto.getStartDate(), ods707eDto.getEndDate());
        Ods707eDto dto = new Ods707eDto();
        log.debug("List:"+ods707eGridDtoList.size());
        dto.setOds707eGridDtoList(ods707eGridDtoList);
        if (ods707eGridDtoList.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return dto;
    }

}
