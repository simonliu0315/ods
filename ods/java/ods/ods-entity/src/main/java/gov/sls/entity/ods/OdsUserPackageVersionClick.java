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
 * The persistent class for the ODS_USER_PACKAGE_VERSION_CLICK database table.
 * 
 */
@Entity
@Table(name="ODS_USER_PACKAGE_VERSION_CLICK")
@NamedQuery(name="OdsUserPackageVersionClick.findAll", query="SELECT o FROM OdsUserPackageVersionClick o")
public class OdsUserPackageVersionClick implements Serializable {
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

    @Column(name="IP_ADDRESS")
    private String ipAddress;

    @Column(name="PACKAGE_ID")
    private String packageId;

    @Column(name="PACKAGE_VER")
    private int packageVer;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    @Column(name="USER_ID")
    private String userId;
    
    @Column(name="USER_ROLE")
    private String userRole;

    public OdsUserPackageVersionClick() {
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

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public int getPackageVer() {
        return this.packageVer;
    }

    public void setPackageVer(int packageVer) {
        this.packageVer = packageVer;
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

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
