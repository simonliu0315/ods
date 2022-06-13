package gov.sls.entity.ods;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="ODS_PACKAGE_DOCUMENT")
public class OdsPackageDocument {

    @Id
    @GenericGenerator(name="generator", strategy="guid", parameters= {})
    @GeneratedValue(generator="generator")
    @Column(name="ID")
    private String id;
    
    @Column(name="PACKAGE_ID")
    private String packageId;
    
    @Column(name="FILENAME")
    private String filename;
    
    @Column(name="DESCRIPTION")
    private String description;
    
    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="UPDATED")
    private Date updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
}
