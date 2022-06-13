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
@Table(name="ODS_CRITERIA")
@IdClass(gov.sls.entity.ods.OdsCriteriaPK.class)
@NamedQuery(name="OdsCriteria.findAll", query="SELECT o FROM OdsCriteria o")
public class OdsCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name="RESOURCE_ID")
    private String resourceId;

    @Id
    @NotNull
    @Column(name="RESOURCE_CRITERIA_ID")
    private String resourceCriteriaId;

    @Id
    @NotNull
    @Column(name="CONDITION")
    private int condition;
    
    @Column(name="AGGREGATE_FUNC")
    private String aggregateFunc;

    @Column(name="CREATE_USER_ID")
    private String createUserId;

    @Column(name="CREATED")
    private Date created;

    @Column(name="DATA_FIELD")
    private String dataField;

    @Column(name="OPERATOR")
    private String operator;

    @Column(name="TARGET")
    private String target;

    @Column(name="UPDATE_USER_ID")
    private String updateUserId;

    @Column(name="UPDATED")
    private Date updated;

    public OdsCriteria() {
    }

    @JsonIgnore
    public OdsCriteriaPK getId() {
        OdsCriteriaPK id = new OdsCriteriaPK();
        id.setResourceId(this.resourceId);
        id.setCondition(this.condition);
        id.setResourceCriteriaId(this.resourceCriteriaId);
        return id;
    }

    public String getAggregateFunc() {
        return this.aggregateFunc;
    }

    public void setAggregateFunc(String aggregateFunc) {
        this.aggregateFunc = aggregateFunc;
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

    public String getDataField() {
        return this.dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
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

    /**
     * @return the resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * @param resourceId the resourceId to set
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }


    /**
     * @return the condition
     */
    public int getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(int condition) {
        this.condition = condition;
    }

    public String getResourceCriteriaId() {
        return resourceCriteriaId;
    }

    public void setResourceCriteriaId(String resourceCriteriaId) {
        this.resourceCriteriaId = resourceCriteriaId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aggregateFunc == null) ? 0 : aggregateFunc.hashCode());
        result = prime * result + condition;
        result = prime * result + ((createUserId == null) ? 0 : createUserId.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((dataField == null) ? 0 : dataField.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result
                + ((resourceCriteriaId == null) ? 0 : resourceCriteriaId.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((updateUserId == null) ? 0 : updateUserId.hashCode());
        result = prime * result + ((updated == null) ? 0 : updated.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OdsCriteria other = (OdsCriteria) obj;
        if (aggregateFunc == null) {
            if (other.aggregateFunc != null)
                return false;
        } else if (!aggregateFunc.equals(other.aggregateFunc))
            return false;
        if (condition != other.condition)
            return false;
        if (createUserId == null) {
            if (other.createUserId != null)
                return false;
        } else if (!createUserId.equals(other.createUserId))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (dataField == null) {
            if (other.dataField != null)
                return false;
        } else if (!dataField.equals(other.dataField))
            return false;
        if (operator == null) {
            if (other.operator != null)
                return false;
        } else if (!operator.equals(other.operator))
            return false;
        if (resourceCriteriaId == null) {
            if (other.resourceCriteriaId != null)
                return false;
        } else if (!resourceCriteriaId.equals(other.resourceCriteriaId))
            return false;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        if (updateUserId == null) {
            if (other.updateUserId != null)
                return false;
        } else if (!updateUserId.equals(other.updateUserId))
            return false;
        if (updated == null) {
            if (other.updated != null)
                return false;
        } else if (!updated.equals(other.updated))
            return false;
        return true;
    }

 

}
