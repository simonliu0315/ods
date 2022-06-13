/**
 * 
 */
package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsUserPackageRate;
import lombok.Data;

@Data
public class Ods310eInitDto {
    private String tip;
    private Boolean anonymousUser;
    private OdsUserPackageRate odsUserPackageRate;
}
