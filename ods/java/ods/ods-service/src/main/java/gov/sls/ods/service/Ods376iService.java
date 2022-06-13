package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods376iQueryDto;
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
public class Ods376iService {

    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    public Ods376iQueryDto query(Ods376iQueryDto ods376iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1502#D05@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        String sYearString = ods376iQueryDto.getDataYrS() + "0101";
        String eYearString = ods376iQueryDto.getDataYrE() + "0101";
        String maskBan = BanMask.getInstance().process(ods376iQueryDto.getBan());
        ods376iQueryDto.setDetails(odsResourceRepository.findOds376Details(resourceId,
                maskBan, sYearString, eYearString));
        return ods376iQueryDto;
    }
    
    /**
     * @param sYearString
     * @param eYearString
     * @return
     */
    public boolean yearVerify(String sYearString, String eYearString) {
        Date sDate = DateUtils.toDate(sYearString + "0101", "yyyyMMdd");
        Date eDate = DateUtils.toDate(eYearString + "0101", "yyyyMMdd");
        if(sDate == null || eDate == null || (sDate.getTime() - eDate.getTime()) >= 0) {
         return false;   
        }else {
            return true;   
        }
    }
}
