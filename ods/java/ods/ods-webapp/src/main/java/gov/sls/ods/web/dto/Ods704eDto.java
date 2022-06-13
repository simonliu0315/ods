package gov.sls.ods.web.dto;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()},
 * {@link #equals(Object)} 及 {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods704eDto {
    private String id;
    private String name;
    private String description;
    private String chkIdentityIdList;
    private String selPackageIdList;
    private String imageUrl;
    
    
    private MultipartFile file;
}
