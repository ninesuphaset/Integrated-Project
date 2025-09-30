package sit.int202.kp2itbmshop.dtos;

import lombok.Data;
import sit.int202.kp2itbmshop.entities.Brand;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleItemWithImageInfo {
    private Integer id;
    private Integer brandId;
    private String model;
    private String description;
    private Integer price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;

    private List<ImageInfoDto> imageInfos;
}
