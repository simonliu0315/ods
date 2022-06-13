package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods378iQueryDto;
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
public class Ods378iService {

    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    public Ods378iQueryDto query(Ods378iQueryDto ods378iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1502#D07@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        String blYmS = ods378iQueryDto.getBlYmS() + "01";
        String blYmE = ods378iQueryDto.getBlYmE() + "01";
        String maskBan = BanMask.getInstance().process(ods378iQueryDto.getBan());
        
        ods378iQueryDto.setDetails(odsResourceRepository.findOds378Details(resourceId,
                maskBan, blYmS, blYmE));
        return ods378iQueryDto;
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
