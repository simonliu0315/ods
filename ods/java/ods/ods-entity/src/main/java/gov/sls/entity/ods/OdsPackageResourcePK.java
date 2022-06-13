package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_PACKAGE_RESOURCE database table.
 * 
 */
@Embeddable
public class OdsPackageResourcePK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="PACKAGE_ID", insertable=false, updatable=false)
    private String packageId;

    @Column(name="PACKAGE_VER", insertable=false, updatable=false)
    private int packageVer;

    @Column(name="RESOURCE_ID", insertable=false, updatable=false)
    private String resourceId;

    @Column(name="RESOURCE_VER", insertable=false, updatable=false)
    private int resourceVer;

    public OdsPackageResourcePK() {
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
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OdsPackageResourcePK other = (OdsPackageResourcePK) obj;
        if (packageId == null) {
            if (other.packageId != null)
                return false;
        } else if (!packageId.equals(other.packageId))
            return false;
        if (packageVer != other.packageVer)
            return false;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        if (resourceVer != other.resourceVer)
            return false;
        return true;
    }

    
}
