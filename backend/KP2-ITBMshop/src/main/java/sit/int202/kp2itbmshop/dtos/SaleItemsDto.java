package sit.int202.kp2itbmshop.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SaleItemsDto {
    private Integer id;
    private String model;
    @JsonIgnore
    private BrandDto brand;
    public String getBrandName() {
        return brand.getName();
    }
    private Integer price;
    private Integer ramGb;
    private Integer storageGb;
    private String color;
}
