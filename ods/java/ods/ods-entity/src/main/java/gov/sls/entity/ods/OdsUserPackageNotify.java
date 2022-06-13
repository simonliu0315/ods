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
 * The persistent class for the ODS_USER_PACKAGE_NOTIFY database table.
 * 
 */
@Entity
@Table(name="ODS_USER_PACKAGE_NOTIFY")
@NamedQuery(name="OdsUserPackageNotify.findAll", query="SELECT o FROM OdsUserPackageNotify o")
public class OdsUserPackageNotify implements Serializable {
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

    @Column(name="EMAIL")
    private String email;

    @Column(name="PACKAGE_ID")
    private String packageId;

    @Column(name="RESOURCE_CRITERIA_ID")
    private String resourceCriteriaId;

    @Column(name="RESOURCE_ID")
    private String resourceId;

    @Column(name="USER_ID")
    private String userId;

    @Column(name="USER_ROLE")
    private String userRole;

    public OdsUserPackageNotify() {
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getResourceCriteriaId() {
        return this.resourceCriteriaId;
    }

    public void setResourceCriteriaId(String resourceCriteriaId) {
        this.resourceCriteriaId = resourceCriteriaId;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return this.userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

}
