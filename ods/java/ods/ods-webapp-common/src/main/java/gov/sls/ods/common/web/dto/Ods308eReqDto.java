package gov.sls.ods.common.web.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ods308eReqDto {

    private String resource_id;
    private int resource_ver;
    private int limit;
    private String q;
    private int offset;
    private Map<String, Object> filters;
    private String sort;
    private boolean plain;
    private String language;
    private List<String> fields;
   
}
