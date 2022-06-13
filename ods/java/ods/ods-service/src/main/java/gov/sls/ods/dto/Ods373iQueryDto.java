package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods373iQueryDto {

    private String v;
    private String code;
    private String msg;
    private String invoiceYmS;
    private String invoiceYmE;
    private String hsnNm;
    private String busiChiNm;
    private String cardTypeNm;
    private List<Map<String,Object>> details;
    
}
