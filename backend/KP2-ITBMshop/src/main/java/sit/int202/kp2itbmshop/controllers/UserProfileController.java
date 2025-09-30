package sit.int202.kp2itbmshop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import sit.int202.kp2itbmshop.dtos.UpdateUserDto;
import sit.int202.kp2itbmshop.entities.User;
import sit.int202.kp2itbmshop.services.UserService;
import sit.int202.kp2itbmshop.utils.JwtUtil;

@RestController
@RequestMapping("/v2/users")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class UserProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token;
        try {
            token = authHeader.replace("Bearer ", "").trim();
            // ตรวจสอบ token validity
            if (token.isEmpty() || jwtUtil.isTokenExpired(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid or Expired token");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid token");
        }

        Integer userIdFromToken;
        try {
            userIdFromToken = jwtUtil.extractUserIdFromAccessToken(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid token");
        }

        // ดึง entity User จาก DB
        User user = userService.getUserById(id);
        if (!user.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not active");
        }

        // เช็ค id จาก token
        if (!id.equals(userIdFromToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Request user id not matched with id in access token");
        }

        // คืน profile DTO
        Object profile = userService.getUserProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Integer id,
            @RequestBody UpdateUserDto updateUserDto,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token;
        try {
            token = authHeader.replace("Bearer ", "").trim();
            if (token.isEmpty() || jwtUtil.isTokenExpired(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Integer userIdFromToken;
        try {
            userIdFromToken = jwtUtil.extractUserIdFromAccessToken(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        User user = userService.getUserById(id);
        if (!user.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not active");
        }

        if (!id.equals(userIdFromToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Request user id not matched with id in access token");
        }

        try {
            User updatedUser = userService.updateUserName(id, updateUserDto);
            Object profile = userService.getUserProfileById(updatedUser.getId());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}





