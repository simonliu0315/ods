package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods377iQueryDto;
import gov.sls.ods.repository.OdsResourceRepository;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.common.utils.DateUtils;
import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods377iService {

    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    public Ods377iQueryDto query(Ods377iQueryDto ods377iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1502#D06@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        String sYearString = ods377iQueryDto.getDataYrS() + "0101";
        String eYearString = ods377iQueryDto.getDataYrE() + "0101";
        ods377iQueryDto.setDetails(odsResourceRepository.findOds377Details(resourceId
                , sYearString, eYearString
                , ods377iQueryDto.getHsnNm(), ods377iQueryDto.getTownNm(), ods377iQueryDto.getBscd2Nm()));
        return ods377iQueryDto;
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
