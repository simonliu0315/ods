package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_RESOURCE_CATEGORY database table.
 * 
 */
@Embeddable
public class OdsResourceCategoryPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="RESOURCE_ID", insertable=false, updatable=false)
    private String resourceId;

    @Column(name="CATEGORY_ID", insertable=false, updatable=false)
    private String categoryId;

    public OdsResourceCategoryPK() {
    }
    public String getResourceId() {
        return this.resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    public String getCategoryId() {
        return this.categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
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
        OdsResourceCategoryPK other = (OdsResourceCategoryPK) obj;
        if (categoryId == null) {
            if (other.categoryId != null)
                return false;
        } else if (!categoryId.equals(other.categoryId))
            return false;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        return true;
    }

    
}
