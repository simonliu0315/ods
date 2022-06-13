/**
 * 
 */
package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsGroup;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


/**
 * 
 */

@Data
public class Ods301e2FormBean {

    //private int aaa;

    private String groupName;
    private int orderByType;
    //private String bbb;
    
    private List<String> selectedOdsIdentity;
    private List<String> selectedOdsPackageTag;
    private List<String> selectedOdsResourceFileExt;
    
    private List<OdsGroup> odsGroups;
    private List<String> odsIdentity;
    private List<String> odsPackageTag;
    private List<String> odsResourceFileExt;
    
    private String btnCommand;
    
    private String btnMoreIdentityTag;
    private int moreIdentityTag;
    
    private String btnMorePkgTag;
    private int morePkgTag;
    
    private String btnMoreResFileExt;
    private int moreResFileExt;
    
    
    public Ods301e2FormBean() {
        selectedOdsIdentity = new ArrayList<String>();
        selectedOdsPackageTag = new ArrayList<String>();
        selectedOdsResourceFileExt = new ArrayList<String>();
        odsIdentity = new ArrayList<String>();
        odsPackageTag = new ArrayList<String>();
        odsResourceFileExt = new ArrayList<String>();
        odsGroups = new ArrayList<OdsGroup>();
    }
    
}
