package sit.int202.kp2itbmshop.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.kp2itbmshop.entities.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.refresh.expiration}")
    private long refreshExpiration;
    @Value("${app.jwt.access.expiration}")
    private long accessExpiration;

    //สร้าง token จาก email
    public String generateRefreshToken(Integer userId, String email) {
        return Jwts.builder()
                .setSubject("email-verification")  //// subject ของ token (ใช้แท็กบอกว่านี่คือโทเค็นสำหรับยืนยันอีเมล)
                .claim("userId", userId)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public String generateAccessToken(Integer userId, String email, String nickName, String userType) {
        return Jwts.builder()
                .setIssuer("http://intproj24.sit.kmutt.ac.th/kp2")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .claim("nickname", nickName)
                .claim("id", userId)
                .claim("email", email)
                .claim("role", userType)
                .claim("type", "ACCESS_TOKEN" )
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    // ดึง Claims ทั้งหมดออกมาจาก token (จะ throw ถ้า token ไม่ถูกต้อง/โดนแก้/หมดอายุ)
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ดึงค่า email ออกจาก claim
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // สำหรับ email verification
    public Integer extractUserIdFromEmailToken(String token) {
        return extractAllClaims(token).get("userId", Integer.class);
    }


    // สำหรับ access token
    public Integer extractUserIdFromAccessToken(String token) {
        return extractAllClaims(token).get("id", Integer.class);
    }

    // เช็คว่า token หมดอายุหรือยัง (true = หมดอายุแล้ว)
    public boolean isTokenExpired(String token) {
        Date exp = extractAllClaims(token).getExpiration();
        return exp.before(new Date());
    }

    public boolean isValidClaims(Map<String, Object> jwtClaims) {
        return jwtClaims.containsKey("iat")
                && "http://intproj24.sit.kmutt.ac.th/kp2".equals(jwtClaims.get("iss"))
                && jwtClaims.containsKey("id")
                && ((Integer) jwtClaims.get("id")) > 0
                && "ACCESS_TOKEN".equals(jwtClaims.get("type"));
    }

    //  สำหรับ Access Token
    public Integer getUserIdFromAccessToken(String token) {
        return extractAllClaims(token).get("id", Integer.class);
    }

    public Integer getUserIdFromRefreshToken(String token) {
        return extractAllClaims(token).get("userId", Integer.class);
    }

    public String getRoleFromAccessToken(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token, User user) {
        Integer userId = getUserIdFromRefreshToken(token);
        return userId.equals(user.getId()) && !isTokenExpired(token);
    }

    public void verifyToken(String token) throws AuthenticationException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token); // verify signature + expired
        } catch (io.jsonwebtoken.JwtException ex) {
            // JwtException รวม SignatureException, ExpiredJwtException, MalformedJwtException
            throw new AuthenticationException("Invalid JWT", ex) {};
        }
    }

    public Map<String, Object> getInfo(String authHeader, Integer pathUserId) {
        String token = authHeader.replace("Bearer ", "").trim();
        Integer userIdFromToken = getUserIdFromAccessToken(token);
        String roleFromToken = getRoleFromAccessToken(token);
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("userId", userIdFromToken);
        tokenInfo.put("role", roleFromToken);

        return tokenInfo;
    }


}