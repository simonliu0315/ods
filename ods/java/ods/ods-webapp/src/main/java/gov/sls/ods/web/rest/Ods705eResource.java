package gov.sls.ods.web.rest;

import gov.sls.entity.ods.OdsLayout;
import gov.sls.ods.Messages;
import gov.sls.ods.repository.OdsLayoutRepository;
import gov.sls.ods.service.Ods705eService;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.web.Alerter;
import com.google.common.base.Strings;

@Slf4j
@Controller
@RequestMapping("ODS705E/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS705E')")
public class Ods705eResource {
    
    @Autowired
    private Ods705eService service;

    @Autowired
    private OdsLayoutRepository repository;
    
    private List<OdsLayout> find(String name, Alerter alerter) {
        // 取得內網專用的國庫署使用者物件
        // 如果登入時使用支用機關、公用事業或其他憑證登入，則會收到 ClassCastException !!!
        // 詳細使用方式請參考 UserHolder.getUser() 的 javadoc，謝謝
//        NtaUser user = UserHolder.getUser();
//        log.trace("User \"{}\" performing query.", user);

        List<OdsLayout> odsLayout = service.findByName(name);
        if (odsLayout.isEmpty()) {
            //alerter.info(Messages.warning_notFound());
            alerter.info(Messages.warning_notFound());
        } else {
            //alerter.success(Messages.success_find());
            alerter.success(Messages.success_find());
        }
        return odsLayout;
    }

    /*
     * 若沒有傳key值，會mapping到此網址，查詢所有資料
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsLayout> find(Alerter alerter) {
        return find(Strings.emptyToNull(null), alerter);
    }

    /*
     * 若有傳key值，會mapping到此網址，查詢單一筆資料
     */
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    OdsLayout get(@PathVariable String name, Alerter alerter) {
        List<OdsLayout> odsLayout = find(name, alerter);
        if (odsLayout.isEmpty()) {
            return null;
        }
        return odsLayout.get(0);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void create(@RequestBody OdsLayout odsLayout, Alerter alerter) {
        String status = service.create(odsLayout);
        if ("".equals(status)) {
            //alerter.success(Messages.success_create());
            alerter.success(Messages.success_create());
        } else {
            alerter.fatal("範本名稱不可重覆!");
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void save(@RequestBody OdsLayout odsLayout, Alerter alerter) {
        service.save(odsLayout);
        //alerter.success(Messages.success_update());
        alerter.success(Messages.success_update());
    }

//    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
//    public @ResponseBody
//    void delete(@PathVariable String id, Alerter alerter) {
//        service.delete(id);
//        alerter.success(Messages.success_delete());
//    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void delete(@RequestBody OdsLayout odsLayout, Alerter alerter) {
        service.delete(odsLayout);
        //alerter.success(Messages.success_delete());
        alerter.success(Messages.success_delete());
    }

    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
    @RequestMapping(value = "/find/all", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsLayout> find(@RequestBody OdsLayout odsLayout, Alerter alerter) {
        return find(odsLayout.getName(), alerter);
    }

}
