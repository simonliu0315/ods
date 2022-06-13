package gov.sls.ods.web.dto;

import gov.sls.ods.dto.PackageInfo;

import java.util.List;

import lombok.Data;

@Data
public class Ods751iDto {
    private List<PackageInfo> packageInfoList;
    private String code;
}
