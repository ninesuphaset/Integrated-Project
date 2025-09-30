package sit.int202.kp2itbmshop.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.kp2itbmshop.entities.User;
import sit.int202.kp2itbmshop.repositories.UserRepository;
import sit.int202.kp2itbmshop.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v2/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class EmailController {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public EmailController(JwtUtil jwtUtil, UserRepository userRepository, ModelMapper modelMapper) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        // ดึง refresh_token จาก Cookie
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        try {
            // Extract userId จาก refresh token
            Integer userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);

            // โหลด user จาก DB
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ตรวจสอบความถูกต้องของ refresh token
            if (!jwtUtil.isTokenValid(refreshToken, user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            // สร้าง access token ใหม่
            String newAccessToken = jwtUtil.generateAccessToken(user.getId(),user.getEmail(),user.getNickName(),user.getUserType());

            // Return access token ใน Response Body
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("access_token", newAccessToken);

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }


//  POST (สำหรับ frontend เรียกตรงตาม requirement)
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmailPost(@RequestParam("token") String token) {
        return doVerify(token);
    }

// GET (สำหรับกดลิงก์จากอีเมลโดยตรง)
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmailGet(@RequestParam("token") String token) {
        return doVerify(token);
    }

//  รวม logic verification
private ResponseEntity<?> doVerify(String token) {
    try {
        if (jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        Integer userId = jwtUtil.extractUserIdFromEmailToken(token);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || !userOpt.get().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token");
        }

        User user = userOpt.get();
        if (user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already verified");
        }

        user.setIsActive(true);
        userRepository.save(user);

        // return แค่ข้อความ
        return ResponseEntity.ok("Verification Successfully");

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token");
    }
}

}

