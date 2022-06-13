package gov.sls.ods.service;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionPK;
import gov.sls.ods.dto.OdsOpenGraphDto;
import gov.sls.ods.dto.PackageAndResourceDto;
import gov.sls.ods.dto.TemplateGeneratorDto;
import gov.sls.ods.repository.OdsPackageMetadataRepository;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsPackageTagRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods304eService {

    @Autowired
    private OdsPackageVersionRepository packageVersionRepos;
    
    @Autowired
    private OdsPackageTagRepository packageTagRepos;
    
    @Autowired
    private OdsPackageMetadataRepository packageMetadataRepos;
    
    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    public TemplateGeneratorDto generateTemplate(String packageId, int ver) {
        
        TemplateGeneratorDto dto = new TemplateGeneratorDto();
        dto.setPublishPackageVerLast(packageVersionRepos.getPublishPackageVerLast(packageId));
        
        OdsPackage odsPackage = odsPackageRepository.findOne(packageId);
        dto.setOdsPackage(odsPackage);

        OdsPackageVersionPK packageVersionPk = new OdsPackageVersionPK();
        packageVersionPk.setPackageId(packageId);
        packageVersionPk.setVer(ver);

        OdsOpenGraphDto ogDto = null;
        OdsPackageVersion packageVersion = packageVersionRepos.findOne(packageVersionPk);
        
        if (packageVersion != null) {
            ogDto = new OdsOpenGraphDto();
            ogDto.setTitle(packageVersion.getName());
            ogDto.setDescription(packageVersion.getDescription());            
            log.debug(packageVersion.getPattern());
            dto.setPublishPackage(packageVersion);
            String row[] = packageVersion.getPattern().split(",");
            Integer[] intarray = new Integer[row.length];
            for (int i = 0; i < row.length; i++) {
                intarray[i] = Integer.parseInt(row[i]);
            }

            int cnt = 0;
            List<List<PackageAndResourceDto>> rowList = new ArrayList<List<PackageAndResourceDto>>();
            for (int i : intarray) {

                List<PackageAndResourceDto> columnList = new ArrayList<PackageAndResourceDto>();
                for (int j = 0; j < i; j++) {

                    List<PackageAndResourceDto> list = packageVersionRepos.findPackageAndResource(
                            packageId, ver, cnt, j);
                    if (list.size() > 0) {
                        PackageAndResourceDto parDto = list.get(0);
                        if("dataset".equals(parDto.getFormat())) {
                            List<Map<String,Object>> grid = odsJdbcTemplate.queryForList("SELECT TOP 10 * FROM ODS_"+parDto.getResourceId().replaceAll("-", "_"), new HashMap<String,Object>());
                            for (Map<String,Object> map :grid) {
                                map.remove("ODS_RESOURCE_VER"); 
                            }
                            if (grid.size() > 0) {                                
                                parDto.setGridTitle(grid.get(0).keySet());
                            }
                            parDto.setGridData(grid);                            
                        } else {
                            parDto.setGridData(new ArrayList<Map<String,Object>>());
                        }
                        columnList.add(parDto);
                    }

                }
                cnt++;
                rowList.add(columnList);
            }
            
            dto.setMainData(rowList);

            dto.setPackageTags(packageTagRepos.findByPackageId(packageId));
            dto.setPackageMetadata(packageMetadataRepos.getUnionPackageExtra(packageId, ver));
        }
        return dto;
    }
}
