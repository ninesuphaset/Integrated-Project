package sit.int202.kp2itbmshop.dtos;

import lombok.Data;

@Data
public class UserProfileDto {
    private Integer id;
    private String email;
    private String fullName;
    private String userType;
    private String nickName;
}
