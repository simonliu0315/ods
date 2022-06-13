package gov.sls.ods.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Ods706eTab2FormBean {

    private String resId;
    private String criId;
    private int condition;
    private String dataField;
    private String aggregateFunc;
    private String operator;
    private BigDecimal target;
    private String rowCount;
    

}
