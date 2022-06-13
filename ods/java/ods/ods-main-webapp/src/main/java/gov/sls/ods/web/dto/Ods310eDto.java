/**
 * 
 */
package gov.sls.ods.web.dto;

import lombok.Data;
import net.sf.json.JSONArray;

@Data
public class Ods310eDto {
    private String packageId;
    private String packageVer;
    private JSONArray dataArray;
    private String startDate;
    private String endDate;
}
