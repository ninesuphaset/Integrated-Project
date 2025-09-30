package sit.int202.kp2itbmshop.dtos;

import lombok.Data;
import java.util.List;

@Data
public class SellerCartDto {
    private Integer sellerId;
    private String sellerName;
    private List<CartItemDto> cart;
}
