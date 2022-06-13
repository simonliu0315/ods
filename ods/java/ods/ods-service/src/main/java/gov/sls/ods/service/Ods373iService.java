package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods373iQueryDto;
import gov.sls.ods.repository.OdsResourceRepository;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.common.utils.DateUtils;
import com.mysema.query.types.expr.BooleanExpression;


@Slf4j
@Service
public class Ods373iService {

    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    public Ods373iQueryDto query(Ods373iQueryDto ods373iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1503#D01@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        ods373iQueryDto.setDetails(odsResourceRepository.findOds373Details(resourceId
                , ods373iQueryDto.getInvoiceYmS(), ods373iQueryDto.getInvoiceYmE()
                , ods373iQueryDto.getHsnNm(), ods373iQueryDto.getBusiChiNm()
                , ods373iQueryDto.getCardTypeNm()));
        return ods373iQueryDto;
    }
    
    public boolean dateVerify(String sDateString, String eDateString) {
        Date sDate = DateUtils.toDate(sDateString, "yyyyMM");
        Date eDate = DateUtils.toDate(eDateString, "yyyyMM");
        if(sDate == null || eDate == null || (sDate.getTime() - eDate.getTime()) > 0) {
         return false;   
        }else {
            return true;   
        }
    }
}
