package gov.sls.ods.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Ods708eGridDto {
    private String serviceName;
    private String methodName;
    private String desc;
    private Date startDate;
    private Date endDate;
}
