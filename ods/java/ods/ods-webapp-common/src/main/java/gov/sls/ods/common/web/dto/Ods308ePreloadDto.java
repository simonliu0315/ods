package gov.sls.ods.common.web.dto;

import gov.sls.ods.dto.PackageAndResourceDto;

import java.util.List;

import lombok.Data;

@Data
public class Ods308ePreloadDto {

    private String id;
    private int ver;
    private boolean datastore_active;
    private String format;
    private String url;
    private String siteUrl;
    private String packageId;
    private int packageVer;
    private List<PackageAndResourceDto> parDtoList;
    private String description;
    private String name;
    private String rowPosition;
    private String columnPosition;
    
}
