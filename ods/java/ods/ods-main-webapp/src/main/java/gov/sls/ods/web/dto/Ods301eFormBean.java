/**
 * 
 */
package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsGroup;
import gov.sls.entity.ods.OdsPackage;
import gov.sls.ods.dto.Ods301eDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Ods301eFormBean {
    private String groupId;
    private String groupName;
    private int orderByType;
    private List<Ods301eDto> odsPackage;
    private OdsGroup odsGroup;
    private String packageId;

    private List<String> selectedOdsIdentity;
    private List<String> selectedOdsPackageTag;
    private List<String> selectedOdsResourceFileExt;

    private List<OdsGroup> odsGroups;
    private List<String> odsIdentity;
    private List<String> odsPackageTag;
    private List<String> odsResourceFileExt;
    
    public String breadLink;
    public List<String[]> funcInfo;

    public Ods301eFormBean() {
        selectedOdsIdentity = new ArrayList<String>();
        selectedOdsPackageTag = new ArrayList<String>();
        selectedOdsResourceFileExt = new ArrayList<String>();
        odsIdentity = new ArrayList<String>();
        odsPackageTag = new ArrayList<String>();
        odsResourceFileExt = new ArrayList<String>();
        odsGroups = new ArrayList<OdsGroup>();
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     *            the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName
     *            the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the selectedOdsIdentity
     */
    public List<String> getSelectedOdsIdentity() {
        return selectedOdsIdentity;
    }

    /**
     * @param selectedOdsIdentity
     *            the selectedOdsIdentity to set
     */
    public void setSelectedOdsIdentity(List<String> selectedOdsIdentity) {
        this.selectedOdsIdentity = selectedOdsIdentity;
    }

    /**
     * @return the selectedOdsPackageTag
     */
    public List<String> getSelectedOdsPackageTag() {
        return selectedOdsPackageTag;
    }

    /**
     * @param selectedOdsPackageTag
     *            the selectedOdsPackageTag to set
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
     * @param selectedOdsResourceFileExt
     *            the selectedOdsResourceFileExt to set
     */
    public void setSelectedOdsResourceFileExt(List<String> selectedOdsResourceFileExt) {
        this.selectedOdsResourceFileExt = selectedOdsResourceFileExt;
    }

    /**
     * @return the odsGroup
     */
    public List<OdsGroup> getOdsGroups() {
        return odsGroups;
    }

    /**
     * @param odsGroup
     *            the odsGroup to set
     */
    public void setOdsGroups(List<OdsGroup> odsGroups) {
        this.odsGroups = odsGroups;
    }

    /**
     * @return the odsIdentity
     */
    public List<String> getOdsIdentity() {
        return odsIdentity;
    }

    /**
     * @param odsIdentity
     *            the odsIdentity to set
     */
    public void setOdsIdentity(List<String> odsIdentity) {
        this.odsIdentity = odsIdentity;
    }

    /**
     * @return the odsPackageTag
     */
    public List<String> getOdsPackageTag() {
        return odsPackageTag;
    }

    /**
     * @param odsPackageTag
     *            the odsPackageTag to set
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
     * @param odsResourceFileExt
     *            the odsResourceFileExt to set
     */
    public void setOdsResourceFileExt(List<String> odsResourceFileExt) {
        this.odsResourceFileExt = odsResourceFileExt;
    }

    /**
     * @return the odsPackage
     */
    public List<Ods301eDto> getOdsPackage() {
        return odsPackage;
    }

    /**
     * @param odsPackage
     *            the odsPackage to set
     */
    public void setOdsPackage(List<Ods301eDto> odsPackage) {
        this.odsPackage = odsPackage;
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
     * @return the odsGroup
     */
    public OdsGroup getOdsGroup() {
        return odsGroup;
    }

    /**
     * @param odsGroup the odsGroup to set
     */
    public void setOdsGroup(OdsGroup odsGroup) {
        this.odsGroup = odsGroup;
    }

    /**
     * @return the packageId
     */
    public String getPackageId() {
        return packageId;
    }

    /**
     * @param packageId the packageId to set
     */
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    

    public List<String[]> getFuncInfo() {
        return funcInfo;
    }

    public void setFuncInfo(List<String[]> funcInfo) {
        this.funcInfo = funcInfo;
    }

    public String getBreadLink() {
        return breadLink;
    }

    public void setBreadLink(String breadLink) {
        this.breadLink = breadLink;
    }
}
