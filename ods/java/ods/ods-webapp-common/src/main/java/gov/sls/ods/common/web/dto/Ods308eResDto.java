package gov.sls.ods.common.web.dto;

import gov.sls.ods.dto.Ods308ePreviewResultDto;

import java.util.List;

import lombok.Data;

@Data
public class Ods308eResDto {
    
    private int offset;
    private int limit;
    private List<String> filters;    
    private Ods308ePreviewResultDto result; 
}
