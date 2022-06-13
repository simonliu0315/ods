package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_GROUP_PACKAGE database table.
 * 
 */
@Embeddable
public class OdsGroupPackagePK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="GROUP_ID", insertable=false, updatable=false)
    private String groupId;

    @Column(name="PACKAGE_ID", insertable=false, updatable=false)
    private String packageId;

    public OdsGroupPackagePK() {
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getPackageId() {
        return this.packageId;
    }
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
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
        OdsGroupPackagePK other = (OdsGroupPackagePK) obj;
        if (groupId == null) {
            if (other.groupId != null)
                return false;
        } else if (!groupId.equals(other.groupId))
            return false;
        if (packageId == null) {
            if (other.packageId != null)
                return false;
        } else if (!packageId.equals(other.packageId))
            return false;
        return true;
    }

    
}
