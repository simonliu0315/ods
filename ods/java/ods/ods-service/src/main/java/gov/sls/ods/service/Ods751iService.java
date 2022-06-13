package gov.sls.ods.service;

import gov.sls.ods.dto.PackageInfo;
import gov.sls.ods.repository.OdsPackageRepository;

import java.text.ParseException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods751iService {

    @Autowired
    private OdsPackageRepository odsPackageRepository;

    public List<PackageInfo> getPackageInfo() throws ParseException {
        
        return odsPackageRepository.findPackageInfo();
    }
}
