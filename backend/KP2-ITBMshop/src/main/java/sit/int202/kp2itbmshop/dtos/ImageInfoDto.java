package sit.int202.kp2itbmshop.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageInfoDto {
    private Integer order;
    private String fileName;
    private String status;
    private MultipartFile imageFile;
}
