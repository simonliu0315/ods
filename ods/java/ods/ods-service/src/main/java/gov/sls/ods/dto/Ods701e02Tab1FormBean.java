package gov.sls.ods.dto;

import gov.sls.entity.ods.OdsCategory;

import java.util.List;

import lombok.Data;

@Data
public class Ods701e02Tab1FormBean {

    private String name;
    private String description;
    //private String delMk;
    private List<OdsCategory> selCategory;

}
