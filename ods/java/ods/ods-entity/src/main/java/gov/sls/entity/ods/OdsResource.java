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
 * The persistent class for the ODS_RESOURCE database table.
 * 
 */
@Entity
@Table(name="ODS_RESOURCE")
@NamedQuery(name="OdsResource.findAll", query="SELECT o FROM OdsResource o")
public class OdsResource implements Serializable {
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

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="FORMAT")
    private String format;

    @Column(name="NAME")
    private String name;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    @Column(name="VIEW_ID")
    private String viewId;

    @Column(name="WORKBOOK_ID")
    private String workbookId;

    public OdsResource() {
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
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

    public String getViewId() {
        return this.viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getWorkbookId() {
        return this.workbookId;
    }

    public void setWorkbookId(String workbookId) {
        this.workbookId = workbookId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OdsResource [id=").append(id).append(", createUserId=")
                .append(createUserId).append(", created=").append(created).append(", description=")
                .append(description).append(", format=").append(format).append(", name=")
                .append(name).append(", updateUserId=").append(updateUserId).append(", updated=")
                .append(updated).append(", viewId=").append(viewId).append(", workbookId=")
                .append(workbookId).append("]");
        return builder.toString();
    }

}
