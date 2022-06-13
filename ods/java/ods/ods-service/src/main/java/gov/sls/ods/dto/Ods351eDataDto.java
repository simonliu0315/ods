package gov.sls.ods.dto;

import com.google.common.base.Strings;

import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()}, {@link #equals(Object)} 及
 * {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods351eDataDto {

    private String packageId;
    private Integer packageVer;
    private String packageUrl;
    private Integer clickCount;
    private String imageUrl;
    private String name;
    private String description;
    private String imageFileName;
    private String type;
    private String code;

    public String getPackageUrl() {
        // /ODS303E/'+${odsPackage.packageId}+'/'+${odsPackage.packageVer}+'/
        if ("02".equals(type) && !Strings.isNullOrEmpty(code)) {
            packageUrl = String.format("/ODS311E/%s/%s/%s/%s/", packageId, packageVer, type, code);
        } else {
            packageUrl = String.format("/ODS303E/%s/%s/", packageId, packageVer);
        }
        
        return packageUrl;
    }

    public String getImageUrl() {
        imageUrl = String.format("/ODS308E/public/package/%s/image/%s", packageId, imageFileName);
        return imageUrl;
    }
    
    public String getDescription() {
        if(description == null || description.length() < 70){
            return description;
        }
        
        return description.substring(0, 70) + "...";
    }
}
