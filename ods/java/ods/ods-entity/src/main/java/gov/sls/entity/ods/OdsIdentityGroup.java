package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the ODS_IDENTITY_GROUP database table.
 * 
 */
@Entity
@Table(name="ODS_IDENTITY_GROUP")
@NamedQuery(name="OdsIdentityGroup.findAll", query="SELECT o FROM OdsIdentityGroup o")
public class OdsIdentityGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OdsIdentityGroupPK id;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    @Column(name="VIEW_PRIORITY")
    private int viewPriority;

    public OdsIdentityGroup() {
    }

    public OdsIdentityGroupPK getId() {
        return this.id;
    }

    public void setId(OdsIdentityGroupPK id) {
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

    public int getViewPriority() {
        return this.viewPriority;
    }

    public void setViewPriority(int viewPriority) {
        this.viewPriority = viewPriority;
    }

}
