package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_NOTICE_PACKAGE_VERSION database table.
 * 
 */
@Embeddable
public class OdsNoticePackageVersionPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="PACKAGE_ID", insertable=false, updatable=false)
    private String packageId;

    @Column(name="PACKAGE_VER", insertable=false, updatable=false)
    private int packageVer;

    public OdsNoticePackageVersionPK() {
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
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
        result = prime * result + packageVer;
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
        OdsNoticePackageVersionPK other = (OdsNoticePackageVersionPK) obj;
        if (packageId == null) {
            if (other.packageId != null)
                return false;
        } else if (!packageId.equals(other.packageId))
            return false;
        if (packageVer != other.packageVer)
            return false;
        return true;
    }

    
}
