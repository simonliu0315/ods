package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods379iQueryDto;
import gov.sls.ods.repository.OdsResourceRepository;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.common.utils.DateUtils;
import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods379iService {

    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    public Ods379iQueryDto query(Ods379iQueryDto ods379iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1502#D08@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        String blYmS = ods379iQueryDto.getBlYmS() + "01";
        String blYmE = ods379iQueryDto.getBlYmE() + "01";
        ods379iQueryDto.setDetails(odsResourceRepository.findOds379Details(resourceId
                , blYmS, blYmE
                , ods379iQueryDto.getHsnNm(), ods379iQueryDto.getTownNm()
                , ods379iQueryDto.getBscd2Nm()));
        return ods379iQueryDto;
    }

    public boolean yearMonthVerify(String blYmS, String blYmE) {
        blYmS = blYmS + "01";
        blYmE = blYmE + "01";
        Date sDate = DateUtils.toDate(blYmS, "yyyyMMdd");
        Date eDate = DateUtils.toDate(blYmE, "yyyyMMdd");
        if(sDate == null || eDate == null || (sDate.getTime() - eDate.getTime()) >= 0) {
         return false;   
        }else {
            return true;   
        }
    }
    
}
