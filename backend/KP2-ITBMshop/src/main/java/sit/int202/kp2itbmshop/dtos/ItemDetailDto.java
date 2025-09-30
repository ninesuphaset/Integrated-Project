package sit.int202.kp2itbmshop.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Data
public class ItemDetailDto {
    private Integer id;
    private String model;
    private String brandName;
    private String description;
    private Integer price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer quantity;
    private Integer storageGb;
    private String color;
    private SellerDto seller;
    private Instant createdOn;
    private Instant updatedOn;

    private String sellerName;

    private List<SaleItemImageDto> saleItemImages;

    @Data
    public static class SaleItemImageDto {
        private String fileName;
        private Integer imageViewOrder;
        private String status;
    }
}
