/**
 * 
 */
package gov.sls.ods.web.dto;

import gov.sls.ods.dto.Ods302eDto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


/**
 * 
 */

@Data
public class Ods302e2FormBean {

    private String packageName;
    private int orderByType;
    
    private List<String> selectedOdsPackageTag;
    private List<String> selectedOdsResourceFileExt;
    
    private List<Ods302eDto> odsPackages;
    private List<String> odsPackageTag;
    private List<String> odsResourceFileExt;
    
    private String btnMorePkgTag;
    private int morePkgTag;
    
    private String btnMoreResFileExt;
    private int moreResFileExt;
    
    public Ods302e2FormBean() {
        selectedOdsPackageTag = new ArrayList<String>();
        selectedOdsResourceFileExt = new ArrayList<String>();
        odsPackageTag = new ArrayList<String>();
        odsResourceFileExt = new ArrayList<String>();
    }
    
    private String btnCommand;
    
}
