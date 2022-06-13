package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods372iQueryDto;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.util.BanMask;

import java.util.Date;
import java.util.List;
import java.util.Map;








import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.common.utils.DateUtils;
import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods372iService {
    
    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    
    public Ods372iQueryDto query(Ods372iQueryDto ods372iQueryDto) {
        BooleanExpression criteria =  QOdsResource.odsResource.viewId.eq("O1503#D01@ALL$01") ;
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if(odsResource != null) {
            resourceId = odsResource.getId();
        }
        ods372iQueryDto.setBan(BanMask.getInstance().process(ods372iQueryDto.getBan())); // ban 加密
//        ods372iQueryDto.setBan("3712");//測試用
        ods372iQueryDto.setDetails(odsResourceRepository.findOds372Details(resourceId
                , ods372iQueryDto.getInvoiceYmS(), ods372iQueryDto.getInvoiceYmE()
                , ods372iQueryDto.getHsnNm(), ods372iQueryDto.getTownNm()
                , ods372iQueryDto.getCardTypeNm(), ods372iQueryDto.getCardCodeNm()
                , ods372iQueryDto.getBusiChiNm(),ods372iQueryDto.getBan()));
        return ods372iQueryDto;
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
