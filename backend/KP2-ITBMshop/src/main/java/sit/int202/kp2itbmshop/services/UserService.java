package sit.int202.kp2itbmshop.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.kp2itbmshop.Exception.ItemNotFoundException;
import sit.int202.kp2itbmshop.dtos.RegisDto;
import sit.int202.kp2itbmshop.dtos.SellerProfileDto;
import sit.int202.kp2itbmshop.dtos.UpdateUserDto;
import sit.int202.kp2itbmshop.dtos.UserProfileDto;
import sit.int202.kp2itbmshop.entities.SaleItem;
import sit.int202.kp2itbmshop.entities.SellerProfile;
import sit.int202.kp2itbmshop.entities.User;
import sit.int202.kp2itbmshop.repositories.SellerProfileRepository;
import sit.int202.kp2itbmshop.repositories.UserRepository;
import sit.int202.kp2itbmshop.utils.JwtUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerProfileRepository sellerProfileRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir-seller:seller-card}")
    private String sellerUploadDir;

    @Value("${app.verify-email.base-url}")
    private String verifyEmailBaseUrl;

    public User saveUser(RegisDto regisDto , MultipartFile frontPhoto, MultipartFile backPhoto) {
        regisDto.setUserId(null);
        User user = new User();
        user.setUserType(regisDto.getUserType());
        user.setNickName(regisDto.getNickName());
        user.setEmail(regisDto.getEmail());

        String hashedPassword = passwordEncoder.encode(regisDto.getPassword());
        user.setPassword(hashedPassword);

        user.setFullName(regisDto.getFullName());
        user.setIsActive(false);

        User saveUser = userRepository.save(user);

        if("SELLER".equalsIgnoreCase(regisDto.getUserType())){
            SellerProfile sellerProfile = new SellerProfile();
            sellerProfile.setSeller_id(null);
            sellerProfile.setUser(saveUser);
            sellerProfile.setPhoneNumber(regisDto.getPhoneNumber());
            sellerProfile.setBankAccount(regisDto.getBankAccount());
            sellerProfile.setBankName(regisDto.getBankName());
            sellerProfile.setIdCardNumber(regisDto.getIdCardNumber());

            try {
                if (!Files.exists(Paths.get(sellerUploadDir))) {
                    Files.createDirectories(Paths.get(sellerUploadDir));
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot create upload dir", e);
            }
            String frontFileName = savePhoto(frontPhoto, sellerUploadDir, saveUser.getId());
            String backFileName = savePhoto(backPhoto, sellerUploadDir, saveUser.getId());

            if (frontFileName == null || backFileName == null) {
                throw new RuntimeException("Both front and back ID photos are required");
            }
            sellerProfile.setIdCardImageFront(frontFileName);
            sellerProfile.setIdCardImageBack(backFileName);
            sellerProfileRepository.save(sellerProfile);
            sellerProfileRepository.save(sellerProfile);
        }

        //สร้าง JWT token โดยใช้ email ของผู้ใช้ที่เพิ่งบันทึก เพื่อเอาไว้สำหรับยืนยันอีเมล
        String token = jwtUtil.generateRefreshToken(saveUser.getId(), saveUser.getEmail());
        String link = verifyEmailBaseUrl + token;

        emailService.sendEmail(saveUser.getEmail(), link);

        return saveUser;
    }

    private String savePhoto(MultipartFile file, String uploadDir, Integer userId) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = userId + "_" + originalFilename;
            java.nio.file.Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            java.nio.file.Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Cannot store file " + file.getOriginalFilename(), e);
        }
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("User not found"));
    }

    public Object getUserProfileById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("User not found"));

        if ("SELLER".equalsIgnoreCase(user.getUserType())) {
            SellerProfile sellerProfile = sellerProfileRepository.findByUser(user)
                    .orElseThrow(() -> new ItemNotFoundException("Seller profile not found"));

            SellerProfileDto sellerDto = new SellerProfileDto();
            sellerDto.setId(user.getId());
            sellerDto.setEmail(user.getEmail());
            sellerDto.setFullName(user.getFullName());
            sellerDto.setUserType(user.getUserType());
            sellerDto.setNickName(user.getNickName());
            sellerDto.setPhoneNumber(sellerProfile.getPhoneNumber());
            sellerDto.setBankName(sellerProfile.getBankName());
            sellerDto.setBankAccount(sellerProfile.getBankAccount());

            return sellerDto;
        } else {
            UserProfileDto buyerDto = new UserProfileDto();
            buyerDto.setId(user.getId());
            buyerDto.setEmail(user.getEmail());
            buyerDto.setFullName(user.getFullName());
            buyerDto.setUserType(user.getUserType());
            buyerDto.setNickName(user.getNickName());

            return buyerDto;
        }
    }

    @Transactional
    public User updateUserName(Integer id, UpdateUserDto dto) {
        User user = getUserById(id);

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getNickName() != null && !dto.getNickName().isBlank()) {
            user.setNickName(dto.getNickName());
        }

        return userRepository.save(user);
    }

}
