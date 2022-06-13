package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods371iQueryDto;
import gov.sls.ods.repository.OdsResourceRepository;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.common.utils.DateUtils;
import com.mysema.query.types.expr.BooleanExpression;



@Slf4j
@Service
public class Ods371iService {
    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    
    public Ods371iQueryDto query(Ods371iQueryDto ods371iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1501#D01@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        ods371iQueryDto.setDetails(odsResourceRepository.findOds371Details(resourceId
                , ods371iQueryDto.getBarcode(), ods371iQueryDto.getInvoiceDateS()
                , ods371iQueryDto.getInvoiceDateE(), ods371iQueryDto.getHsnNm()
                , ods371iQueryDto.getTownNm(), ods371iQueryDto.getCardTypeNm()
                , ods371iQueryDto.getCardCodeNm()));
        return ods371iQueryDto;
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
