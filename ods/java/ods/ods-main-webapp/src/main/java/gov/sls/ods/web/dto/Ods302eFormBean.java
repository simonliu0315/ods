/**
 * 
 */
package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.ods.dto.Ods302eDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Ods302eFormBean {
    private String packageName;
    private int orderByType;
    private List<Ods302eDto> odsPackages;

    private List<String> selectedOdsPackageTag;
    private List<String> selectedOdsResourceFileExt;

    private List<String> odsPackageTag;
    private List<String> odsResourceFileExt;

    public Ods302eFormBean() {
        selectedOdsPackageTag = new ArrayList<String>();
        selectedOdsResourceFileExt = new ArrayList<String>();
        odsPackageTag = new ArrayList<String>();
        odsResourceFileExt = new ArrayList<String>();
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the orderByType
     */
    public int getOrderByType() {
        return orderByType;
    }

    /**
     * @param orderByType the orderByType to set
     */
    public void setOrderByType(int orderByType) {
        this.orderByType = orderByType;
    }

    /**
     * @return the odsPackages
     */
    public List<Ods302eDto> getOdsPackages() {
        return odsPackages;
    }

    /**
     * @param odsPackages the odsPackages to set
     */
    public void setOdsPackages(List<Ods302eDto> odsPackages) {
        this.odsPackages = odsPackages;
    }

    /**
     * @return the selectedOdsPackageTag
     */
    public List<String> getSelectedOdsPackageTag() {
        return selectedOdsPackageTag;
    }

    /**
     * @param selectedOdsPackageTag the selectedOdsPackageTag to set
     */
    public void setSelectedOdsPackageTag(List<String> selectedOdsPackageTag) {
        this.selectedOdsPackageTag = selectedOdsPackageTag;
    }

    /**
     * @return the selectedOdsResourceFileExt
     */
    public List<String> getSelectedOdsResourceFileExt() {
        return selectedOdsResourceFileExt;
    }

    /**
     * @param selectedOdsResourceFileExt the selectedOdsResourceFileExt to set
     */
    public void setSelectedOdsResourceFileExt(List<String> selectedOdsResourceFileExt) {
        this.selectedOdsResourceFileExt = selectedOdsResourceFileExt;
    }

    /**
     * @return the odsPackageTag
     */
    public List<String> getOdsPackageTag() {
        return odsPackageTag;
    }

    /**
     * @param odsPackageTag the odsPackageTag to set
     */
    public void setOdsPackageTag(List<String> odsPackageTag) {
        this.odsPackageTag = odsPackageTag;
    }

    /**
     * @return the odsResourceFileExt
     */
    public List<String> getOdsResourceFileExt() {
        return odsResourceFileExt;
    }

    /**
     * @param odsResourceFileExt the odsResourceFileExt to set
     */
    public void setOdsResourceFileExt(List<String> odsResourceFileExt) {
        this.odsResourceFileExt = odsResourceFileExt;
    }

}
