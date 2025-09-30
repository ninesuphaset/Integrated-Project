package sit.int202.kp2itbmshop.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.kp2itbmshop.dtos.RegisDto;
import sit.int202.kp2itbmshop.dtos.UserLogInDto;
import sit.int202.kp2itbmshop.dtos.UserResponseDto;
import sit.int202.kp2itbmshop.dtos.UserResponseLogin;
import sit.int202.kp2itbmshop.entities.User;
import sit.int202.kp2itbmshop.services.AuthenticationService;
import sit.int202.kp2itbmshop.services.UserService;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v2/auth")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class UserRegisController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;

    private static final int EMAIL_MAX_LENGTH = 50;
    private static final int PASSWORD_MAX_LENGTH = 14;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public UserRegisController(UserService userService, AuthenticationService authenticationService, ModelMapper modelMapper) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(path = "/register",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> addUser(
            @ModelAttribute RegisDto RegisDto,
            @RequestParam(value = "idCardImageFront", required = false) MultipartFile frontPhoto,
            @RequestParam(value = "idCardImageBack", required = false) MultipartFile backPhoto
    ){
        User saveUser = userService.saveUser(RegisDto,frontPhoto,backPhoto);
        UserResponseDto userResponseDto = modelMapper.map(saveUser,UserResponseDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseLogin> authentication(
            @RequestBody UserLogInDto userLoginDto) {

        String email = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();

        if (email == null || email.length() > EMAIL_MAX_LENGTH || !EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (password == null || password.trim().isEmpty() || password.length() > PASSWORD_MAX_LENGTH) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        UserResponseLogin response = authenticationService.authenticate(userLoginDto);
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", response.getRefresh_token())
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public  ResponseEntity<Void> logout(){
            ResponseCookie deleteCookie = ResponseCookie.from("refresh_token","")
                    .httpOnly(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(0)
                    .build();

            return ResponseEntity.noContent()
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .build();
    }

}
