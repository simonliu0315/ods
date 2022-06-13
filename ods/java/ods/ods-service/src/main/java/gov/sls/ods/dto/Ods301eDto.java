package gov.sls.ods.dto;

import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()},
 * {@link #equals(Object)} 及 {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods301eDto {

    private String id;
    private String imageUrl;
    private String name;
    private String description;
    private Integer latestVer;
    private String type;
    private String code;
    
    public String getDescription() {
        if(description == null || description.length() < 39){
            return description;
        }
        
        return description.substring(0, 39) + "...";
    }
}
