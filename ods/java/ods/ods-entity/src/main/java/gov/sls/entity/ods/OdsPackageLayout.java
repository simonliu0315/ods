package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the ODS_PACKAGE_LAYOUT database table.
 * 
 */
@Entity
@Table(name="ODS_PACKAGE_LAYOUT")
@NamedQuery(name="OdsPackageLayout.findAll", query="SELECT o FROM OdsPackageLayout o")
public class OdsPackageLayout implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OdsPackageLayoutPK id;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="RESOURCE_ID")
    private String resourceId;

    @Column(name="RESOURCE_VER")
    private int resourceVer;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    public OdsPackageLayout() {
    }

    public OdsPackageLayoutPK getId() {
        return this.id;
    }

    public void setId(OdsPackageLayoutPK id) {
        this.id = id;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceVer() {
        return this.resourceVer;
    }

    public void setResourceVer(int resourceVer) {
        this.resourceVer = resourceVer;
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

}
