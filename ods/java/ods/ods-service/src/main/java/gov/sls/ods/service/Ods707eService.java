package gov.sls.ods.service;

import gov.sls.ods.dto.Ods707eGridDto;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsUserFollowPackageRepository;
import gov.sls.ods.repository.OdsUserPackageRateRepository;
import gov.sls.ods.repository.OdsUserPackageVersionClickRepository;

import java.math.BigDecimal;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods707eService {

    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    @Autowired
    private OdsUserPackageRateRepository odsUserPackageRateRepository;
    
    @Autowired
    private OdsUserPackageVersionClickRepository odsUserPackageVersionClickRepository;
    
    @Autowired
    private OdsUserFollowPackageRepository odsUserFollowPackageRepository;

    public List<Ods707eGridDto> findGridData(String name, String desc, String sDate, String eDate) {
        log.info("eDate:{}", eDate);
        List<Ods707eGridDto> ods707eGridDtoList = odsPackageRepository.findPackages(name, desc, sDate, eDate);
        if (!ods707eGridDtoList.isEmpty()) {
            for (Ods707eGridDto dto : ods707eGridDtoList) {
                //find avgRate and totCount
                List<Ods707eGridDto> avgRateAndTotCount = odsUserPackageRateRepository.findAvgRateAneTotCount(dto.getPackageId(), sDate, eDate);
                if (!avgRateAndTotCount.isEmpty()) {
                    String roundAvgRate = String.valueOf(new BigDecimal(avgRateAndTotCount.get(0).getAvgRate()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    dto.setAvgRate(roundAvgRate);
                    dto.setTotCount(avgRateAndTotCount.get(0).getTotCount());
                }                
                //find clickCount
                List<Ods707eGridDto> clickCount = odsUserPackageVersionClickRepository.findClickCount(dto.getPackageId(), sDate, eDate);
                if (!clickCount.isEmpty()) {
                    dto.setClickCount(clickCount.get(0).getClickCount());
                }
                //find followCount
                List<Ods707eGridDto> followCount = odsUserFollowPackageRepository.findFollowCount(dto.getPackageId(), sDate, eDate);
                if (!followCount.isEmpty()) {
                    dto.setFollowCount(followCount.get(0).getFollowCount());
                }
            }
        }
        return ods707eGridDtoList;
    }
}
