package gov.sls.ods.web.dto;

import gov.sls.ods.dto.RSSItem;

import java.util.List;

import lombok.Data;

@Data
public class Ods356iDto {

    private List<RSSItem> rssItemList;
    private String code;
    
}
