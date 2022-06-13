package gov.sls.entity.ods;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(name="ODS_XCA_DONATE_GOAL")
@IdClass(gov.sls.entity.ods.OdsXcaDonateGoalPK.class)
@NamedQuery(name="OdsXcaDonateGoal.findAll", query="SELECT o FROM OdsXcaDonateGoal o")
public class OdsXcaDonateGoal implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull    
    @Column(name="ASSOCIATION_BAN")
    private String associationBan;

    @Id
    @NotNull    
    @Column(name="YEAR")
    private String year;

    @Column(name="DONATE_COUNT")
    private BigDecimal donateCount;

    public OdsXcaDonateGoal() {
    }

    @JsonIgnore
    public OdsXcaDonateGoalPK getId() {
        OdsXcaDonateGoalPK id = new OdsXcaDonateGoalPK();
        id.setAssociationBan(this.associationBan);
        id.setYear(this.year);
        return id;
    }
    
    @JsonIgnore
    public void setId(OdsXcaDonateGoalPK id) {
        this.setAssociationBan(id.getAssociationBan());
        this.setYear(id.getYear());
    }

    /**
     * @return the associationBan
     */
    public String getAssociationBan() {
        return associationBan;
    }


    /**
     * @param associationBan the associationBan to set
     */
    public void setAssociationBan(String associationBan) {
        this.associationBan = associationBan;
    }


    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }


    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the donateCount
     */
    public BigDecimal getDonateCount() {
        return donateCount;
    }

    /**
     * @param donateCount the donateCount to set
     */
    public void setDonateCount(BigDecimal donateCount) {
        this.donateCount = donateCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((associationBan == null) ? 0 : associationBan.hashCode());
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OdsXcaDonateGoal)) {
            return false;
        }
        OdsXcaDonateGoal other = (OdsXcaDonateGoal) obj;
        if (associationBan == null) {
            if (other.associationBan != null) {
                return false;
            }
        } else if (!associationBan.equals(other.associationBan)) {
            return false;
        }
        if (year == null) {
            if (other.year != null) {
                return false;
            }
        } else if (!year.equals(other.year)) {
            return false;
        }
        return true;
    }


}
