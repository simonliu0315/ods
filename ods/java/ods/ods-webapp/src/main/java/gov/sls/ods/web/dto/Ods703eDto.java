package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageDocument;
import gov.sls.entity.ods.OdsPackageExtra;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.ods.dto.Ods703eGridDto;
import gov.sls.ods.dto.Ods703eTab2Dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Ods703eDto {
    private OdsPackage odsPackage;
    private List<OdsPackage> odsPackageList;
    private List<Ods703eGridDto> packageAndVersionList;
    private List<OdsPackageTag> odsPackageTagList;
    private List<Ods703eTab2Dto> packageMetatemplateDto;
    private List<OdsPackageExtra> odsPackageExtraList;
    private List<OdsPackageDocument> odsPackageDocumentList;
    private String ver;
    private String versionDatetime;
    private String isUaaCheck;
    
    public Ods703eDto() {
        odsPackage = new OdsPackage();
    }

    public String getId() {
        return odsPackage.getId();
    }

    public void setId(String id) {
        odsPackage.setId(id);
    }

    public String getCreateUserId() {
        return odsPackage.getCreateUserId();
    }

    public void setCreateUserId(String createUserId) {
        odsPackage.setCreateUserId(createUserId);
    }

    public Date getCreated() {
        return odsPackage.getCreated();
    }

    public void setCreated(Date created) {
        odsPackage.setCreated(created);
    }

    public String getDescription() {
        return odsPackage.getDescription();
    }

    public void setDescription(String description) {
        odsPackage.setDescription(description);
    }

    public String getImageUrl() {
        return odsPackage.getImageUrl();
    }

    public void setImageUrl(String imageUrl) {
        odsPackage.setImageUrl(imageUrl);
    }

    public String getName() {
        return odsPackage.getName();
    }

    public void setName(String name) {
        odsPackage.setName(name);
    }

    public String getUpdateUserId() {
        return odsPackage.getUpdateUserId();
    }

    public void setUpdateUserId(String updateUserId) {
        odsPackage.setUpdateUserId(updateUserId);
    }

    public Date getUpdated() {
        return odsPackage.getUpdated();
    }

    public void setUpdated(Date updated) {
        odsPackage.setUpdated(updated);
    }

    public String getCode() {
        return odsPackage.getCode();
    }

    public void setCode(String code) {
        odsPackage.setCode(code);
    }

    public String getType() {
        return odsPackage.getType();
    }

    public void setType(String type) {
        odsPackage.setType(type);
    }
}
