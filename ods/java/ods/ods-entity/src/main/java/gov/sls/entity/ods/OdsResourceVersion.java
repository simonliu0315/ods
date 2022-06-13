package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the ODS_RESOURCE_VERSION database table.
 * 
 */
@Entity
@Table(name="ODS_RESOURCE_VERSION")
@NamedQuery(name="OdsResourceVersion.findAll", query="SELECT o FROM OdsResourceVersion o")
public class OdsResourceVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OdsResourceVersionPK id;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="DEL_MK")
    private boolean delMk;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="NAME")
    private String name;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    @Column(name="URL")
    private String url;

    @Column(name="VERSION_DATETIME")
    private String versionDatetime;

    @Column(name="WORKBOOK_VER")
    private Integer workbookVer;

    public OdsResourceVersion() {
    }

    public OdsResourceVersionPK getId() {
        return this.id;
    }

    public void setId(OdsResourceVersionPK id) {
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

    public boolean getDelMk() {
        return this.delMk;
    }

    public void setDelMk(boolean delMk) {
        this.delMk = delMk;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionDatetime() {
        return this.versionDatetime;
    }

    public void setVersionDatetime(String versionDatetime) {
        this.versionDatetime = versionDatetime;
    }

    public Integer getWorkbookVer() {
        return this.workbookVer;
    }

    public void setWorkbookVer(Integer workbookVer) {
        this.workbookVer = workbookVer;
    }

}
