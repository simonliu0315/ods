package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods374iQueryDto;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.util.BanMask;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.common.utils.DateUtils;
import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods374iService {

    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    public Ods374iQueryDto query(Ods374iQueryDto ods374iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1502#D02@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        ods374iQueryDto.setBan(BanMask.getInstance().process(ods374iQueryDto.getBan())); // ban 加密
//        ods374iQueryDto.setBan("3712");//測試用
        ods374iQueryDto.setDetails(odsResourceRepository.findOds374Details(resourceId
                , ods374iQueryDto.getInvoiceDateS(), ods374iQueryDto.getInvoiceDateE()
                , ods374iQueryDto.getBan()));
        return ods374iQueryDto;
    }
    
    public boolean dateVerify(String sDateString, String eDateString) {
        Date sDate = DateUtils.toDate(sDateString, "yyyyMMdd");
        Date eDate = DateUtils.toDate(eDateString, "yyyyMMdd");
        if(sDate == null || eDate == null || (sDate.getTime() - eDate.getTime()) > 0) {
         return false;   
        }else {
            return true;   
        }
    }
}
