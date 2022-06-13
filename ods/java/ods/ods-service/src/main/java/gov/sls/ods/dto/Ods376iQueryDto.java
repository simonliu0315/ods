package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods376iQueryDto {

    private String v;
    private String code;
    private String msg;
    private String ban;
    private String dataYrS;
    private String dataYrE;
    private List<Map<String,Object>> details;
    
}
