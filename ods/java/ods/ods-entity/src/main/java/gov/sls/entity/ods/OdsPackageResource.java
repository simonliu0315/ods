package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the ODS_PACKAGE_RESOURCE database table.
 * 
 */
@Entity
@Table(name="ODS_PACKAGE_RESOURCE")
@IdClass(gov.sls.entity.ods.OdsPackageResourcePK.class)
@NamedQuery(name="OdsPackageResource.findAll", query="SELECT o FROM OdsPackageResource o")
public class OdsPackageResource implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull    
    @Column(name="PACKAGE_ID")
    private String packageId;

    @Id
    @NotNull
    @Column(name="PACKAGE_VER")
    private int packageVer;

    @Id
    @NotNull
    @Column(name="RESOURCE_ID")
    private String resourceId;

    @Id
    @NotNull
    @Column(name="RESOURCE_VER")
    private int resourceVer;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    public OdsPackageResource() {
    }

    @JsonIgnore
    public OdsPackageResourcePK getId() {
        OdsPackageResourcePK id = new OdsPackageResourcePK();
        id.setPackageId(this.packageId);
        id.setPackageVer(this.packageVer);
        id.setResourceId(this.resourceId);
        id.setResourceVer(this.resourceVer);
        return id;
    }
    
    @JsonIgnore
    public void setId(OdsPackageResourcePK id) {
        this.setPackageId(id.getPackageId());
        this.setPackageVer(id.getPackageVer());
        this.setResourceId(id.getResourceId());
        this.setResourceVer(id.getResourceVer());
    }
    public String getPackageId() {
        return this.packageId;
    }
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    public int getPackageVer() {
        return this.packageVer;
    }
    public void setPackageVer(int packageVer) {
        this.packageVer = packageVer;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
        result = prime * result + packageVer;
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + resourceVer;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OdsPackageResource)) {
            return false;
        }
        OdsPackageResource other = (OdsPackageResource) obj;
        if (packageId == null) {
            if (other.packageId != null) {
                return false;
            }
        } else if (!packageId.equals(other.packageId)) {
            return false;
        }
        if (packageVer != other.packageVer) {
            return false;
        }
        if (resourceId == null) {
            if (other.resourceId != null) {
                return false;
            }
        } else if (!resourceId.equals(other.resourceId)) {
            return false;
        }
        if (resourceVer != other.resourceVer) {
            return false;
        }
        return true;
    }
    
}
