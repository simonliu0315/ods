package gov.sls.ods.web.dto;

import gov.sls.ods.dto.EPaper;

import java.util.List;

import lombok.Data;

@Data
public class Ods354iDto {

    private List<EPaper> ePaperList;
    private String code;
    
}
