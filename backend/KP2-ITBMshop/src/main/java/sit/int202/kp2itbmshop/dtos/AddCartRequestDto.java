package sit.int202.kp2itbmshop.dtos;

import lombok.Data;

@Data
public class AddCartRequestDto {
    private Integer userId;
    private Integer saleItemId;
    private Integer quantity;
}
