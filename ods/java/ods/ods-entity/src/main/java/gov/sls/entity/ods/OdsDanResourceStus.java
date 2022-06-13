package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Entity implementation class for Entity: OdsDanView
 *
 */
@Entity
@Table(name = "ODS_DAN_RESOURCE_STUS")
public class OdsDanResourceStus implements Serializable {

    public enum RESOURCE_TYPE {
        WORKBOOK("1"), VIEW("2"), TABLE("3");
        private String resourceType;

        RESOURCE_TYPE(String resourceType) {
            this.resourceType = resourceType;
        }

        public String toString() {
            return this.resourceType;
        }

    }

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "ID")
    private String id;

    @Column(name = "RESOURCE_ID")
    private String resourceId;

    @Column(name = "RESOURCE_TYPE")
    private String resourceType;

    @Column(name = "REFRESH_TIME")
    private Date refreshTime;

    @Column(name = "EXPORT_STUS")
    private String exportStus;

    @Column(name = "IMPORT_ODS_STUS")
    private String importOdsStus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Date getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String getExportStus() {
        return exportStus;
    }

    public void setExportStus(String exportStus) {
        this.exportStus = exportStus;
    }

    public String getImportOdsStus() {
        return importOdsStus;
    }

    public void setImportOdsStus(String importOdsStus) {
        this.importOdsStus = importOdsStus;
    }

    @Override
    public String toString() {
        return "OdsDanResourceStus [id=" + id + ", resourceId=" + resourceId
                + ", resourceType=" + resourceType + ", refreshTime="
                + refreshTime + ", exportStus=" + exportStus
                + ", importOdsStus=" + importOdsStus + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((exportStus == null) ? 0 : exportStus.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((importOdsStus == null) ? 0 : importOdsStus.hashCode());
        result = prime * result
                + ((refreshTime == null) ? 0 : refreshTime.hashCode());
        result = prime * result
                + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result
                + ((resourceType == null) ? 0 : resourceType.hashCode());
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
        OdsDanResourceStus other = (OdsDanResourceStus) obj;
        if (exportStus == null) {
            if (other.exportStus != null)
                return false;
        } else if (!exportStus.equals(other.exportStus))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (importOdsStus == null) {
            if (other.importOdsStus != null)
                return false;
        } else if (!importOdsStus.equals(other.importOdsStus))
            return false;
        if (refreshTime == null) {
            if (other.refreshTime != null)
                return false;
        } else if (!refreshTime.equals(other.refreshTime))
            return false;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        if (resourceType == null) {
            if (other.resourceType != null)
                return false;
        } else if (!resourceType.equals(other.resourceType))
            return false;
        return true;
    }

}
