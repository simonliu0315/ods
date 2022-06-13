package gov.sls.ods.dto;

import lombok.Data;

@Data
public class Ods303eAnalysisDto {

    private String workbookId;
    private int workbookVer;
    private String name;
    private String packageId;
    private String packageVer;
    private String resourceId;
    private String resourceVer;
}
