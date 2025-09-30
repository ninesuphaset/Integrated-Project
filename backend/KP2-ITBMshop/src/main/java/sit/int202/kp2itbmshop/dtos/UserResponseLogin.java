package sit.int202.kp2itbmshop.dtos;

import lombok.Data;

@Data
public class UserResponseLogin {
    private String access_token;
    private String refresh_token;
}
