package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the ODS_PACKAGE_VERSION database table.
 * 
 */
@Entity
@Table(name="ODS_PACKAGE_VERSION")
@IdClass(gov.sls.entity.ods.OdsPackageVersionPK.class)
@NamedQuery(name="OdsPackageVersion.findAll", query="SELECT o FROM OdsPackageVersion o")
public class OdsPackageVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name="PACKAGE_ID", insertable=false, updatable=false)
    private String packageId;

    @Id
    @NotNull
    @Column(name="VER")
    private int ver;
    
    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="DEL_MK")
    private boolean delMk;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="IS_PUBLISHED")
    private boolean isPublished;

    @Column(name="NAME")
    private String name;

    @Column(name="PATTERN")
    private String pattern;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    @Column(name="VERSION_DATETIME")
    private Date versionDatetime;

    /**
     * 
     */
    public OdsPackageVersion() {
    }

    /**
     * @return OdsPackageVersionPK
     */
    public OdsPackageVersionPK getId() {
        OdsPackageVersionPK id = new OdsPackageVersionPK();
        id.setPackageId(this.packageId);
        id.setVer(this.ver);
        return id;
    }
    
    public void setId(OdsPackageVersionPK odsPackageVersionPK) {
        this.packageId = odsPackageVersionPK.getPackageId();
        this.ver = odsPackageVersionPK.getVer();
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

    /**
     * @return the ver
     */
    public int getVer() {
        return ver;
    }

    /**
     * @param ver the ver to set
     */
    public void setVer(int ver) {
        this.ver = ver;
    }

    /**
     * @param isPublished the isPublished to set
     */
    public void setPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getCreateUserId() {
        return this.createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean getDelMk() {
        return this.delMk;
    }

    public void setDelMk(boolean delMk) {
        this.delMk = delMk;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsPublished() {
        return this.isPublished;
    }

    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getUpdateUserId() {
        return this.updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getVersionDatetime() {
        return this.versionDatetime;
    }

    public void setVersionDatetime(Date versionDatetime) {
        this.versionDatetime = versionDatetime;
    }

}
