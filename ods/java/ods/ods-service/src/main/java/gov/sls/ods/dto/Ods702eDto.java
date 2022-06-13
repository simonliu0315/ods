package gov.sls.ods.dto;



import java.util.List;

import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()}, {@link #equals(Object)} 及
 * {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods702eDto {

    private String categoryId;
    private String categoryName;
    private String categoryDescription;
    
    private List<Ods702eGrid1Dto> grid1;
 }
