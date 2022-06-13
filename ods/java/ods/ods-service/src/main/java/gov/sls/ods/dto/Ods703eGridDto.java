package gov.sls.ods.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Ods703eGridDto {

    private String id;
    private String createUserId;
    private Date created;
    private String description;
    private String imageUrl;
    private String name;
    private String updateUserId;
    private Date updated;
    private String ver;
    private Date versionDatetime;
    private String type;
    private String code;
    
}
