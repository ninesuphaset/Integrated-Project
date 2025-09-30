package sit.int202.kp2itbmshop.dtos;

import lombok.Data;

@Data
public class AddUpdateBrandDto {
    private Integer id;
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;
}
