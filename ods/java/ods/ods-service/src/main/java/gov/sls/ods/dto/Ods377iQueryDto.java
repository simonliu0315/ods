package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods377iQueryDto {

    private String v;
    private String code;
    private String msg;
    private String dataYrS;
    private String dataYrE;
    private String hsnNm;
    private String townNm;
    private String bscd2Nm;
    private List<Map<String,Object>> details;
    
}
