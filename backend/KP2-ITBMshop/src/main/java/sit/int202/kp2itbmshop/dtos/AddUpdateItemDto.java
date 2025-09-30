package sit.int202.kp2itbmshop.dtos;

import lombok.Data;
import sit.int202.kp2itbmshop.entities.Brand;

import java.math.BigDecimal;

@Data
public class AddUpdateItemDto {
    private Integer id;
    private String model;
    private Brand brand;
    private String description;
    private Integer price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer quantity;
    private Integer storageGb;
    private String color;
}
