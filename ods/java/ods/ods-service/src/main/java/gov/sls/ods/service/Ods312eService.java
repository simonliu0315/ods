package gov.sls.ods.service;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.ods.repository.OdsPackageRepository;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods312eService {
    
    @Autowired
    private OdsPackageRepository odsPackageRepository;

    public List<OdsPackage> getPackageById(String packageId) {
        
        return odsPackageRepository.findById(packageId);
    }

}
