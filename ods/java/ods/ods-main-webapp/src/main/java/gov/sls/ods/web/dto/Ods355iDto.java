package gov.sls.ods.web.dto;

import gov.sls.ods.dto.PortalStat;

import java.util.List;

import lombok.Data;

@Data
public class Ods355iDto {

    private List<PortalStat> portalStatList;
    private String code;
    
}
