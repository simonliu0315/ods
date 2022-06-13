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
 * The persistent class for the ODS_PACKAGE_VERSION_EXTRA database table.
 * 
 */
@Entity
@Table(name="ODS_PACKAGE_VERSION_EXTRA")
@NamedQuery(name="OdsPackageVersionExtra.findAll", query="SELECT o FROM OdsPackageVersionExtra o")
public class OdsPackageVersionExtra implements Serializable {
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

    @Column(name="DATA_KEY")
    private String dataKey;

    @Column(name="DATA_TYPE")
    private String dataType;

    @Column(name="DATA_VALUE")
    private String dataValue;

    @Column(name="PACKAGE_ID")
    private String packageId;

    @Column(name="PACKAGE_VER")
    private int packageVer;

    @Column(name="POSITIONS")
    private int positions;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    public OdsPackageVersionExtra() {
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

    public String getDataKey() {
        return this.dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataValue() {
        return this.dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
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

    public int getPositions() {
        return this.positions;
    }

    public void setPositions(int positions) {
        this.positions = positions;
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
