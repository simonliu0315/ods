package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_CRITERIA database table.
 * 
 */
@Embeddable
public class OdsXcaDonateGoalPK implements Serializable {
    private static final long serialVersionUID = 4556278989625639007L;

    @Column(name="ASSOCIATION_BAN", insertable=false, updatable=false)
    private String associationBan;

    @Column(name="YEAR", insertable=false, updatable=false)
    private String year;


    public OdsXcaDonateGoalPK() {
    }


    /**
     * @param associationBan
     * @param year
     */
    public OdsXcaDonateGoalPK(String associationBan, String year) {
        this.associationBan = associationBan;
        this.year = year;
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
        if (!(obj instanceof OdsXcaDonateGoalPK)) {
            return false;
        }
        OdsXcaDonateGoalPK other = (OdsXcaDonateGoalPK) obj;
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
