package sit.int202.kp2itbmshop.dtos;

import lombok.Data;

@Data
public class SellerProfileDto extends UserProfileDto {
    private String phoneNumber;
    private String bankName;
    private String bankAccount;
}
