/**
 * 
 */
package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsUserPackageRate;
import lombok.Data;
import net.sf.json.JSONArray;

@Data
public class Ods315eDto {
    private String packageId;
    private String packageVer;
    private JSONArray dataArray;
    private String startDate;
    private String endDate;
    private String tip;
    private String role;
    private OdsUserPackageRate odsUserPackageRate;
    private String chartType;
}
