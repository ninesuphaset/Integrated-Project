package sit.int202.kp2itbmshop.dtos;


import lombok.Data;

@Data
public class BrandDetailDto {
    private Integer id;
    private String name;
    private String countryOfOrigin;
    private String websiteUrl;
    private Boolean isActive;
    private Integer noOfSaleItems;
}
