package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_RESOURCE_VERSION database table.
 * 
 */
@Embeddable
public class OdsResourceVersionPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="RESOURCE_ID", insertable=false, updatable=false)
    private String resourceId;

    @Column(name="VER")
    private int ver;

    public OdsResourceVersionPK() {
    }
    public String getResourceId() {
        return this.resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
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
        OdsResourceVersionPK other = (OdsResourceVersionPK) obj;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        if (ver != other.ver)
            return false;
        return true;
    }

    
}
