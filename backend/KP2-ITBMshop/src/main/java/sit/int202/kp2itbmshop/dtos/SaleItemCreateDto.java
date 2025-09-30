package sit.int202.kp2itbmshop.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleItemCreateDto {
    @NotNull(message = "Missing request parameters brandId")
    private Integer brandId;
    @NotNull(message = "Missing request parameters model")
    private String model;
    @NotNull(message = "Missing request parameters description")
    private String description;
    @NotNull(message = "Missing request parameters price")
    private Integer price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;
    private List<ImageInfoDto> imageInfos;
}
