package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_CRITERIA database table.
 * 
 */
@Embeddable
public class OdsCodesPK implements Serializable {
    private static final long serialVersionUID = 4556278989625639007L;

    @Column(name="CODE_TYPE")
    private String codeType;

    @Column(name="VALUE")
    private String value;


    public OdsCodesPK() {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codeType == null) ? 0 : codeType.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        if (!(obj instanceof OdsCodesPK)) {
            return false;
        }
        OdsCodesPK other = (OdsCodesPK) obj;
        if (codeType == null) {
            if (other.codeType != null) {
                return false;
            }
        } else if (!codeType.equals(other.codeType)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
    
}
