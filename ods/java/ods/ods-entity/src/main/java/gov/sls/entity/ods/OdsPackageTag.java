package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * The persistent class for the ODS_PACKAGE_TAGS database table.
 * 
 */
@Entity
@Table(name="ODS_PACKAGE_TAGS")
@NamedQuery(name="OdsPackageTag.findAll", query="SELECT o FROM OdsPackageTag o")
public class OdsPackageTag implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name="generator", strategy="guid", parameters= {})
    @GeneratedValue(generator="generator")
    @Column(name="ID")
    private String id;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="PACKAGE_ID")
    private String packageId;

    @Column(name="TAG_NAME")
    private String tagName;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    public OdsPackageTag() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateUserId() {
        return this.createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getTagName() {
        return this.tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getUpdateUserId() {
        return this.updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

}
