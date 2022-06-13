package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_PACKAGE_VERSION database table.
 * 
 */
@Embeddable
public class OdsPackageVersionPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="PACKAGE_ID", insertable=false, updatable=false)
    private String packageId;

    @Column(name="VER")
    private int ver;

    public OdsPackageVersionPK() {
    }
    public String getPackageId() {
        return this.packageId;
    }
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    public int getVer() {
        return this.ver;
    }
    public void setVer(int ver) {
        this.ver = ver;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
        result = prime * result + ver;
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
        OdsPackageVersionPK other = (OdsPackageVersionPK) obj;
        if (packageId == null) {
            if (other.packageId != null)
                return false;
        } else if (!packageId.equals(other.packageId))
            return false;
        if (ver != other.ver)
            return false;
        return true;
    }

    
}
