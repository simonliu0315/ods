package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_CRITERIA database table.
 * 
 */
@Embeddable
public class OdsCriteriaPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="RESOURCE_ID", insertable=false, updatable=false)
    private String resourceId;

    @Column(name="RESOURCE_CRITERIA_ID", insertable=false, updatable=false)
    private String resourceCriteriaId;

    @Column(name="CONDITION")
    private int condition;

    public OdsCriteriaPK() {
    }
    public String getResourceId() {
        return this.resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getCondition() {
        return this.condition;
    }
    public void setCondition(int condition) {
        this.condition = condition;
    }
    public String getResourceCriteriaId() {
        return resourceCriteriaId;
    }
    public void setResourceCriteriaId(String resourceCriteriaId) {
        this.resourceCriteriaId = resourceCriteriaId;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + condition;
        result = prime * result
                + ((resourceCriteriaId == null) ? 0 : resourceCriteriaId.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
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
        OdsCriteriaPK other = (OdsCriteriaPK) obj;
        if (condition != other.condition)
            return false;
        if (resourceCriteriaId == null) {
            if (other.resourceCriteriaId != null)
                return false;
        } else if (!resourceCriteriaId.equals(other.resourceCriteriaId))
            return false;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        return true;
    }
    

    
}
