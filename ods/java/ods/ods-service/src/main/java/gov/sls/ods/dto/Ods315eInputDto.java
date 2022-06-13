package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods315eInputDto {
    private String chartType;
    private String packageId;
    private String packageVer;
    private String invoiceSDate;
    private String invoiceEDate;
    private String hsnCd;
    private String townCd;
    private String businessHsnCd;
    private String businessTownCd;
    private boolean corporation;
    private String selfCounty;
    private String selfArea;
    private String selfIndustClass;
    private String ycolumn;
    private String aggreFun;
    private String ban;
    private String industry;
}
