package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the ODS_CRITERIA database table.
 * 
 */
@Entity
@Table(name="ODS_CODES")
@IdClass(gov.sls.entity.ods.OdsCodesPK.class)
@NamedQuery(name="OdsCodes.findAll", query="SELECT o FROM OdsCodes o")
public class OdsCodes implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name="CODE_TYPE")
    private String codeType;

    @Id
    @NotNull
    @Column(name="VALUE")
    private String value;

    @Column(name="CHN_NAME")
    private String chnName;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    public OdsCodes() {
    }

    @JsonIgnore
    public OdsCodesPK getId() {
        OdsCodesPK id = new OdsCodesPK();
        id.setCodeType(this.codeType);
        id.setValue(this.value);
        return id;
    }
    
    /**
     * @return the chnName
     */
    public String getChnName() {
        return chnName;
    }

    /**
     * @param chnName the chnName to set
     */
    public void setChnName(String chnName) {
        this.chnName = chnName;
    }

    /**
     * @return the createUserId
     */
    public String getCreateUserId() {
        return createUserId;
    }

    /**
     * @param createUserId the createUserId to set
     */
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the updateUserId
     */
    public String getUpdateUserId() {
        return updateUserId;
    }

    /**
     * @param updateUserId the updateUserId to set
     */
    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    /**
     * @return the updated
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    /**
     * @return the codeType
     */
    public String getCodeType() {
        return codeType;
    }

    /**
     * @param codeType the codeType to set
     */
    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OdsCodes [codeType=");
        builder.append(codeType);
        builder.append(", value=");
        builder.append(value);
        builder.append(", chnName=");
        builder.append(chnName);
        builder.append(", createUserId=");
        builder.append(createUserId);
        builder.append(", created=");
        builder.append(created);
        builder.append(", updateUserId=");
        builder.append(updateUserId);
        builder.append(", updated=");
        builder.append(updated);
        builder.append("]");
        return builder.toString();
    }
}
