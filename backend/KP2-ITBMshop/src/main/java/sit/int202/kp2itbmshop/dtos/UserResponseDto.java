package sit.int202.kp2itbmshop.dtos;

import lombok.Data;

@Data
public class UserResponseDto {
    private Integer userId;
    private String nickname;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Boolean isActive;
    private String userType;
}
