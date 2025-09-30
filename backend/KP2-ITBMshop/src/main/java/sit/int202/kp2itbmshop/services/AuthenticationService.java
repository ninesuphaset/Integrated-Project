package sit.int202.kp2itbmshop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.kp2itbmshop.dtos.UserLogInDto;
import sit.int202.kp2itbmshop.dtos.UserResponseLogin;
import sit.int202.kp2itbmshop.entities.User;
import sit.int202.kp2itbmshop.repositories.UserRepository;
import sit.int202.kp2itbmshop.utils.JwtUtil;

@Service
public class AuthenticationService {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public AuthenticationService(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public UserResponseLogin authenticate(UserLogInDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not found"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not active");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getNickName(), user.getUserType());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());

        UserResponseLogin response = new UserResponseLogin();
        response.setAccess_token(accessToken);
        response.setRefresh_token(refreshToken);
        return response;
    }
}
