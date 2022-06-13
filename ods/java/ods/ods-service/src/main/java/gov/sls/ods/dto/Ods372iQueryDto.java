package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods372iQueryDto {
    private String v;
    private String code;
    private String msg;
    private String invoiceYmS;
    private String invoiceYmE;
    private String busiChiNm;
    private String hsnNm;
    private String townNm;
    private String cardTypeNm;
    private String cardCodeNm;
    private String ban;
    private List<Map<String,Object>> details;


}
