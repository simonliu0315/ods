package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_IDENTITY_GROUP database table.
 * 
 */
@Embeddable
public class OdsIdentityGroupPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="IDENTITY_ID", insertable=false, updatable=false)
    private String identityId;

    @Column(name="GROUP_ID", insertable=false, updatable=false)
    private String groupId;

    public OdsIdentityGroupPK() {
    }
    public String getIdentityId() {
        return this.identityId;
    }
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result + ((identityId == null) ? 0 : identityId.hashCode());
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
        OdsIdentityGroupPK other = (OdsIdentityGroupPK) obj;
        if (groupId == null) {
            if (other.groupId != null)
                return false;
        } else if (!groupId.equals(other.groupId))
            return false;
        if (identityId == null) {
            if (other.identityId != null)
                return false;
        } else if (!identityId.equals(other.identityId))
            return false;
        return true;
    }

    
}
