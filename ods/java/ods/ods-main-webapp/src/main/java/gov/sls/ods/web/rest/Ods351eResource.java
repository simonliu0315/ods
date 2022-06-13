package gov.sls.ods.web.rest;

import gov.sls.ods.dto.Ods351eDataDto;
import gov.sls.ods.service.Ods351eService;
import gov.sls.ods.web.dto.Ods351eDto;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("ODS351E/rest")
public class Ods351eResource {

    @Autowired
    private Ods351eService service;

    /**
     * 載入主題列表頁面
     * 
     * @return the page
     */
    @RequestMapping(value = "findLatest", method = RequestMethod.GET)
    public ModelAndView findLastest() {
        Ods351eDto model = new Ods351eDto();
        model.setLatestOdsPackageVersion(service.getLatestOdsPackageVersion());

        ModelAndView mav = new ModelAndView("ods351e/ods351e_02", "data", model);
        return mav;
    }
    
    @RequestMapping(value = "findPopular", method = RequestMethod.GET)
    public ModelAndView findPopulate() {
        Ods351eDto model = new Ods351eDto();
        model.setPopularUserPackageClick(service.getPopularUserPackageClick());

        ModelAndView mav = new ModelAndView("ods351e/ods351e_01", "data", model);
        return mav;
    }
    
    
    @RequestMapping(value = "findLatestData", method = RequestMethod.GET)
    public @ResponseBody List<Ods351eDataDto> findLastestData() {
        return service.getLatestOdsPackageVersion();
    }
    
    @RequestMapping(value = "findPopularData", method = RequestMethod.GET)
    public @ResponseBody List<Ods351eDataDto> findPopularData() {
        return service.getPopularUserPackageClick();
    }
}
