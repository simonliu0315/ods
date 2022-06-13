package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods378iQueryDto {

    private String v;
    private String ban;
    private String code;
    private String msg;
    private String blYmS;
    private String blYmE;
    private List<Map<String,Object>> details;
    
}
