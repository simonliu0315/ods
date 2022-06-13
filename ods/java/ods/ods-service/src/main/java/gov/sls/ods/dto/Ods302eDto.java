package gov.sls.ods.dto;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()},
 * {@link #equals(Object)} 及 {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods302eDto {

    private String id;
    private String imageUrl;
    private String name;
    private String description;
    private Integer latestVer;
    private String type;
    private String code;
    
    
    public String getBreadLink()
    {
        String url = "2;"; 

        try {
            return Base64.encodeBase64String(url.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }
    
    public String getDescription() {
        if(description == null || description.length() < 39){
            return description;
        }
        
        return description.substring(0, 39) + "...";
    }
}
