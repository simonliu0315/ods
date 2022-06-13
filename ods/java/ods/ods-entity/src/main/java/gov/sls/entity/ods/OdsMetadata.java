package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;


/**
 * The persistent class for the ODS_METADATA database table.
 * 
 */
@Entity
@Table(name="ODS_METADATA")
@NamedQuery(name="OdsMetadata.findAll", query="SELECT o FROM OdsMetadata o")
public class OdsMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name="generator", strategy="guid", parameters= {})
    @GeneratedValue(generator="generator")
    @Column(name="ID")
    private String id;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private String created;

    @Column(name="IS_CHOOSE")
    private boolean isChoose;

    @Column(name="META_TYPE")
    private String metaType;

    @Column(name="NAME")
    private String name;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private String updated;

    public OdsMetadata() {
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

    public String getCreated() {
        return this.created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean getIsChoose() {
        return this.isChoose;
    }

    public void setIsChoose(boolean isChoose) {
        this.isChoose = isChoose;
    }

    public String getMetaType() {
        return this.metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    public Object getName() {
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

    public String getUpdated() {
        return this.updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

}
