package gov.sls.ods.dto;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()}, {@link #equals(Object)} 及
 * {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods701eGrid1Dto {

    private int rowCount; 
    private String id;
    private String name;
    private String description;
    private String workbookName;
    private String viewName;
    private String format;
    private String maxResVerCreated;
    private String maxResVer;
    //private boolean delMk;
    //private String delMk;
    private String isChange;
    private String resCategory;
    private List<Map<String, Object>> unSelCategory;
    private List<Map<String, Object>> selCategory;
 }
