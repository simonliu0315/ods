package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the ODS_NOTICE_PACKAGE_VERSION database table.
 * 
 */
@Entity
@Table(name="ODS_NOTICE_PACKAGE_VERSION")
@NamedQuery(name="OdsNoticePackageVersion.findAll", query="SELECT o FROM OdsNoticePackageVersion o")
public class OdsNoticePackageVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OdsNoticePackageVersionPK id;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    public OdsNoticePackageVersion() {
    }

    public OdsNoticePackageVersionPK getId() {
        return this.id;
    }

    public void setId(OdsNoticePackageVersionPK id) {
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
