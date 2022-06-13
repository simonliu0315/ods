package gov.sls.ods.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods308ePreviewResultDto {

    private int total;
    private List<Ods308ePreviewFieldsDto> fields;
    private List<Map<String,Object>> records;
}
