package gov.sls.ods.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Ods773xIndividualDto {

    private String userUnifyId;

    private String code;
    private String name;
    private String createUserId;
    private Date created;
    private String email;
    private String packageId;
    private String resourceCriteriaId;
    private String resourceId;
    private String userId;
    private String userRole;
}
