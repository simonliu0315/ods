package gov.sls.ods.web.dto;

import gov.sls.ods.dto.Ods351eDataDto;

import java.util.List;

import lombok.Data;

@Data
public class Ods351eDto {

    private List<Ods351eDataDto> popularUserPackageClick;
    private List<Ods351eDataDto> latestOdsPackageVersion;
    
}
