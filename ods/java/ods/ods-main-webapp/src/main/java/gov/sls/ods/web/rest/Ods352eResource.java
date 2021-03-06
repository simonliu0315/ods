package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.commons.service.AnonymousUserService;
import gov.sls.ods.Messages;
import gov.sls.ods.dto.Ods352eDataDto;
import gov.sls.ods.service.Ods352eService;
import gov.sls.ods.service.Ods704eService;
import gov.sls.ods.web.dto.Ods352eDto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cht.commons.web.Alerter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Slf4j
@Controller
@RequestMapping("ODS352E/rest")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS352E')")
public class Ods352eResource {

    @Autowired
    private Ods352eService ods352eService;
        
    @Autowired
    private Ods704eService service;

    @Autowired
    private AnonymousUserService anonymousUserService;
    

    @RequestMapping(value="/find_user_follow_pkg",method =RequestMethod.GET)  
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        SlsUser user = UserHolder.getUser();
        boolean isPreview = false;
        
        List<Ods352eDataDto> ods352eDtoList = null;
        log.info("user:{}", user);
        log.info("anonymousUserService.isAnonymousUser:{}", anonymousUserService.isAnonymousUser(user));
        if(anonymousUserService.isAnonymousUser(user)){
            ods352eDtoList = new ArrayList<Ods352eDataDto>();
        } else {
            ods352eDtoList = ods352eService.getOdsUserFollowPackageByUser(user.getId(), isPreview);
        }
        
        log.info("ods352eDtoList size", ods352eDtoList.size());

        // ??????????????????????????????json????????????json-lib
        JSONObject jsonObj = new JSONObject();
        // ??????jqGrid???JSON????????????????????????jsonObj??????
        jsonObj.put("page", 1);                // ?????????
        jsonObj.put("total", 1);        // ?????????
        jsonObj.put("records", ods352eDtoList.size());        // ????????????
        // ??????rows???????????????
        JSONArray rows = new JSONArray();
        // ??????4?????????
        for(int i = 0; i < ods352eDtoList.size(); i++)
        {
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!??????????????????????????????!!!   http://rritw.com/a/JAVAbiancheng/JAVAzonghe/20121116/255099.html
            int j = i + 1;
            Ods352eDataDto ods352eDto = ods352eDtoList.get(i);
                // ???????????????????????????
                JSONObject cell = new JSONObject();
                cell.put("id", j);
                cell.put("name", ods352eDto.getPkgName());
                cell.put("updated", ods352eDto.getUfpUpdated());
                cell.put("pkgId", ods352eDto.getPkgId());
                // ??????????????????rows???
                rows.add(cell);
        }
        
      
        // ???rows??????json?????????
        jsonObj.put("rows", rows);
        // ????????????????????????????????????json????????????????????????
        ////System.out.println("????????????json?????????\n" + jsonObj.toString());
        // ??????????????????
        resp.setCharacterEncoding("UTF-8");
        // ??????json???????????????PrintWriter?????????
        resp.getWriter().print(jsonObj);
    }
    
    
    
    @RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String delete(@RequestBody Ods352eDataDto ods352eDto, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        ////System.out.println ("AAAAAAAAA"+ ods352eDto.getSelPkgIdList());
        log.info("userUnifyId:" + user.getBarCode());
//        user.setBarCode("00000"); //for test
        ods352eService.deleteUserFollowPackageByUserIdPackageIdList(user.getId(), user.getBarCode(), ods352eDto.getSelPkgIdList());
        alerter.success(Messages.success_delete());
        return "AAAA";
    }
    
    
    @RequestMapping(value = "/user_follow_pkg", method = { RequestMethod.GET,
            RequestMethod.POST })
    public ModelAndView getTop5UserFollowPkg(HttpServletRequest request)
            throws JsonParseException, JsonMappingException, UnsupportedEncodingException,
            IOException {
        SlsUser user = UserHolder.getUser();
        //System.out.println ("AAAAAAAAA"+ user.getId());
        boolean isPreview = true;
        
        Ods352eDto model = new Ods352eDto();
        
        List<Ods352eDataDto> ods352eDtoList = ods352eService.getOdsUserFollowPackageByUser(user.getId(), isPreview);
        model.setTop5UserFollowPackage(ods352eDtoList);
        
        //System.out.println ("BBBBBBBBBBBBBBB"+ ods352eDtoList);
        ModelAndView mav = new ModelAndView("ods352e/ods352e_02", "data", model);
        return mav;
    }
    
}
