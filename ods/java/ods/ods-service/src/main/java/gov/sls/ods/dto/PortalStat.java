package gov.sls.ods.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PortalStat {

    private String name;
    private Integer number;
    private Date endDate;
    private String unit;
    
}
