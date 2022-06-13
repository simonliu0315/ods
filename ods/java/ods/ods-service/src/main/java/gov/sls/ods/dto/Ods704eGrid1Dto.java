package gov.sls.ods.dto;



import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()}, {@link #equals(Object)} 及
 * {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods704eGrid1Dto {

    private String pkgGupName;
    private String pkgGupDescription;
    private String pkgGupPackageId;
    private String pkgGupImageUrl;
 }
