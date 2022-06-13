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
public class Ods701eDto {
    private String name;
    private String description;
    private String toDatastoreSync;
    private String toDatastoreDate;
    private String selCategoryIdList;
    private MultipartFile file;
    
    
    private String selImportInfoList;
}
