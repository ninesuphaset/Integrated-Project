package sit.int202.kp2itbmshop.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisDto {
    private Integer userId;
    private String userType;
    private String nickName;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String bankName;
    private String bankAccount;
    private String idCardNumber;
    private MultipartFile idCardImageFront;
    private MultipartFile idCardImageBack;
}
