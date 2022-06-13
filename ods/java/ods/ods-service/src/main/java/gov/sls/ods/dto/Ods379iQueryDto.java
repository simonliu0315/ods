package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods379iQueryDto {

    private String v;
    private String code;
    private String msg;
    private String blYmS;
    private String blYmE;
    private String hsnNm;
    private String townNm;
    private String bscd2Nm;
    private List<Map<String,Object>> details;
    
}
