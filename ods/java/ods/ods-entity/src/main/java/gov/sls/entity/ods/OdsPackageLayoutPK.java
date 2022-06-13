package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the ODS_PACKAGE_LAYOUT database table.
 * 
 */
@Embeddable
public class OdsPackageLayoutPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="PACKAGE_ID", insertable=false, updatable=false)
    private String packageId;

    @Column(name="PACKAGE_VER", insertable=false, updatable=false)
    private int packageVer;

    @Column(name="ROW_POSITION")
    private int rowPosition;

    @Column(name="COLUMN_POSITION")
    private int columnPosition;

    public OdsPackageLayoutPK() {
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
    public int getRowPosition() {
        return this.rowPosition;
    }
    public void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
    }
    public int getColumnPosition() {
        return this.columnPosition;
    }
    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + columnPosition;
        result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
        result = prime * result + packageVer;
        result = prime * result + rowPosition;
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
        OdsPackageLayoutPK other = (OdsPackageLayoutPK) obj;
        if (columnPosition != other.columnPosition)
            return false;
        if (packageId == null) {
            if (other.packageId != null)
                return false;
        } else if (!packageId.equals(other.packageId))
            return false;
        if (packageVer != other.packageVer)
            return false;
        if (rowPosition != other.rowPosition)
            return false;
        return true;
    }

    
}
