package gov.sls.ods.dto;

import lombok.Data;

@Data
public class EPaper {

    private String packageId;
    private String name;
    private String description;
    private String imageUrl;
    private String link;
    private String packageVer;
    private String code;
    private String type;
}
