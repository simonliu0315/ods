package gov.sls.ods.dto;

import lombok.Data;

@Data
public class RSSItem {

    private String title;
    private String description;
    private String packageId;
    private String link;
    
}
