package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsPackageLayout;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionExtra;
import gov.sls.entity.ods.OdsPackageVersionPK;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.dto.Ods703eTab2Dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Ods703e02Dto {
    private OdsPackageVersion odsPackageVersion;
    private OdsPackageVersionPK odsPackageVersionPK;
    private List<OdsPackageVersion> odsPackageVersionList;
    private List<Ods703eTab2DialogDto> ods703eTab2DialogDtoList;
    private List<OdsPackageLayout> odsPackageLayoutList;
    private List<Ods703eTab2Dto> packageMetatemplateDto;
    private List<OdsPackageVersionExtra> odsPackageVersionExtraList;
    private String ctx;

    public Ods703e02Dto() {
        odsPackageVersion = new OdsPackageVersion();
        odsPackageVersionPK = new OdsPackageVersionPK();
        odsPackageVersion.setId(odsPackageVersionPK);
    }

    public OdsPackageVersionPK getId() {
        return odsPackageVersion.getId();
    }

    public void setId(OdsPackageVersionPK id) {
        odsPackageVersion.setId(id);
    }

    public String getCreateUserId() {
        return odsPackageVersion.getCreateUserId();
    }

    public void setCreateUserId(String createUserId) {
        odsPackageVersion.setCreateUserId(createUserId);
    }

    public Date getCreated() {
        return odsPackageVersion.getCreated();
    }

    public void setCreated(Date created) {
        odsPackageVersion.setCreated(created);
    }

    public boolean getDelMk() {
        return odsPackageVersion.getDelMk();
    }

    public void setDelMk(boolean delMk) {
        odsPackageVersion.setDelMk(delMk);
    }

    public String getDescription() {
        return odsPackageVersion.getDescription();
    }

    public void setDescription(String description) {
        odsPackageVersion.setDescription(description);
    }

    public boolean getIsPublished() {
        return odsPackageVersion.getIsPublished();
    }

    public void setIsPublished(boolean isPublished) {
        odsPackageVersion.setIsPublished(isPublished);
    }

    public String getName() {
        return odsPackageVersion.getName();
    }

    public void setName(String name) {
        odsPackageVersion.setName(name);
    }

    public String getPattern() {
        return odsPackageVersion.getPattern();
    }

    public void setPattern(String pattern) {
        odsPackageVersion.setPattern(pattern);
    }

    public String getUpdateUserId() {
        return odsPackageVersion.getUpdateUserId();
    }

    public void setUpdateUserId(String updateUserId) {
        odsPackageVersion.setUpdateUserId(updateUserId);
    }

    public Date getUpdated() {
        return odsPackageVersion.getUpdated();
    }

    public void setUpdated(Date updated) {
        odsPackageVersion.setUpdated(updated);
    }

    public Date getVersionDatetime() {
        return odsPackageVersion.getVersionDatetime();
    }

    public void setVersionDatetime(Date versionDatetime) {
        odsPackageVersion.setVersionDatetime(versionDatetime);
    }

    public String getPackageId() {
        return odsPackageVersionPK.getPackageId();
    }

    public void setPackageId(String packageId) {
        odsPackageVersionPK.setPackageId(packageId);
    }

    public int getVer() {
        return odsPackageVersionPK.getVer();
    }

    public void setVer(int ver) {
        odsPackageVersionPK.setVer(ver);
    }
}
