package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods374iQueryDto {

    private String v;
    private String code;
    private String msg;
    private String invoiceDateS;
    private String invoiceDateE;
    private String ban;
    private List<Map<String,Object>> details;
    
}
