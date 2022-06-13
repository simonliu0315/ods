package gov.sls.ods.dto;



import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()}, {@link #equals(Object)} 及
 * {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods706eGrid4Dto {

    private int rowCount; 
    private String condition;
    private String dataField;
    private String aggregateFunc;
    private String operator;
    private String target;
 }
