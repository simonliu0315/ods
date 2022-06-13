/**
 * 
 */
package gov.sls.ods.web.dto;

import java.util.List;

import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.ods.dto.Ods303eIndividualDto;
import lombok.Data;

@Data
public class Ods311eDto {
    private OdsPackageVersion odsPackageVersion;
    private String packageId;
    private String packageVer;
    private List<Ods303eIndividualDto> resourceDateList;
}
