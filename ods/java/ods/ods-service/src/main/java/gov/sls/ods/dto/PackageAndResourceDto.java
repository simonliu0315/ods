package gov.sls.ods.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class PackageAndResourceDto {

    private String packageId;
    private String packageVer;
    private BigDecimal rowPosition;
    private BigDecimal columnPosition;
    private String resourceId;
    private String resourceVer;
    private String description;
    private String format;
    private String name;
    private String resourceDescription;
    private List<Map<String, Object>> gridData;
    private Set<String> gridTitle;
    private String pattern;
    private int datasetOrder;
    private String currencyCols;
}
