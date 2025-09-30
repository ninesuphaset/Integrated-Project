package sit.int202.kp2itbmshop.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CartItemDto {
    private Integer cartItemId;
    private Integer userId;
    private Integer saleitemId;
    private Integer quantity;
    private String brandName;
    private String model;
    private Integer storageGb;
    private String color;
    private Integer price;
    private Integer stock;

    private List<CartItemDto.SaleItemImageDto> saleItemImages;

    @Data
    public static class SaleItemImageDto {
        private String fileName;
        private Integer imageViewOrder;
        private String status;
    }
}

