package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_WORKBOOK database table.
 * 
 */
@Embeddable
public class OdsWorkbookPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="ID")
    private String id;

    @Column(name="VER")
    private int ver;

    public OdsWorkbookPK() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        OdsWorkbookPK other = (OdsWorkbookPK) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (ver != other.ver)
            return false;
        return true;
    }

    
}
