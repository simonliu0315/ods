package gov.sls.ods.common.web.controller;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.ods.Messages;
import gov.sls.ods.common.web.dto.Ods308eReqDto;
import gov.sls.ods.common.web.dto.Ods308eResDto;
import gov.sls.ods.service.Ods308eService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("api")
// @PreAuthorize("hasAuthority('AUTHORITY_ODS308E')")
public class OdsApiController {

    @Autowired
    private Ods308eService ods308eService;

    @RequestMapping(value = "/3/action/datastore_search", method = { RequestMethod.GET,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods308eResDto datastoreSearch(@RequestBody Ods308eReqDto ods308eReqDto,
            Alerter alerter) {
        log.debug("***datastoreSearch***" + ods308eReqDto);
        SlsUser user = UserHolder.getUser();
        Ods308eResDto ret = new Ods308eResDto();
        
        ret.setResult(ods308eService.getPreviewData(ods308eReqDto.getResource_id(),
                ods308eReqDto.getResource_ver(), ods308eReqDto.getFilters(),
                ods308eReqDto.getOffset(), ods308eReqDto.getLimit(), ods308eReqDto.getSort(), ods308eReqDto.getQ()));
        alerter.success(Messages.success_find());
        return ret;
    }

    @RequestMapping(value = "/i18n/{translate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void il8n(@PathVariable("translate") String translate, Alerter alerter) {
        log.debug("***il8n***");
        System.out.println("***il8n***");
        SlsUser user = UserHolder.getUser();
        log.debug(translate);
        //alerter.success(Messages.success_update());        
    }
}
