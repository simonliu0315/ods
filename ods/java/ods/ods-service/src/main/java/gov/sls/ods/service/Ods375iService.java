package gov.sls.ods.service;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods375iQueryDto;
import gov.sls.ods.repository.OdsResourceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.common.utils.DateUtils;
import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods375iService {

    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    public Ods375iQueryDto query(Ods375iQueryDto ods375iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1502#D04@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        ods375iQueryDto.setDetails(odsResourceRepository.findOds375Details(resourceId
                , ods375iQueryDto.getInvoiceDateS(), ods375iQueryDto.getInvoiceDateE()
                , ods375iQueryDto.getHsnNm(), ods375iQueryDto.getTownNm()
                , ods375iQueryDto.getInvType(), ods375iQueryDto.getBscd2Nm()));
        return ods375iQueryDto;
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
