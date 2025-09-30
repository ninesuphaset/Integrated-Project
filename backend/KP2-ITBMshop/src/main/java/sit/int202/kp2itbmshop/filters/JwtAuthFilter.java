package sit.int202.kp2itbmshop.filters;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.kp2itbmshop.services.JwtUserDetailsService;
import sit.int202.kp2itbmshop.utils.JwtUtil;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        response.setHeader("request-uri", request.getRequestURI());
        final String requestTokenHeader = request.getHeader("Authorization");
        Integer userId = null;
        Claims claims = null;
        Integer pathSellerId = null;

        try {
            // ตรวจสอบ JWT token
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);

                jwtUtils.verifyToken(jwtToken);
                claims = jwtUtils.extractAllClaims(jwtToken);

                if (jwtUtils.isTokenExpired(jwtToken)) {
                    throw new org.springframework.security.core.AuthenticationException("JWT token has expired") {};
                }

                String type = (String) claims.get("type");
                if (!"ACCESS_TOKEN".equals(type)) {
                    throw new org.springframework.security.core.AuthenticationException(
                            "Invalid token type. Access token required") {};
                }

                userId = (Integer) claims.get("id");
            }

            String uri = request.getRequestURI();
            String[] parts = uri.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("sellers".equals(parts[i]) && i + 1 < parts.length) {
                    pathSellerId = Integer.parseInt(parts[i + 1]);
                    break;
                }
            }

            // ตรวจสอบ JWT กับ userDetails
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (userId != null && authentication == null) {
                UserDetails userDetails = this.jwtUserDetailsService.loadUserById(userId);
                if (userDetails == null || !userDetails.getUsername().equals(claims.get("email"))) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Request Seller id not matched with id in access token");
                }

                UsernamePasswordAuthenticationToken upAuthToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                upAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(upAuthToken);
            }

            // ตรวจสอบ seller id ใน path สำหรับ GET /v2/sellers/{id}/sale-items
            if (pathSellerId != null && "GET".equalsIgnoreCase(request.getMethod())) {
                if (userId != null && pathSellerId != userId) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Cannot access sale items from another seller");
                }
            }

            chain.doFilter(request, response);

        } catch (ResponseStatusException ex) {
            SecurityContextHolder.clearContext();
            response.setContentType("application/json");
            response.setStatus(ex.getStatusCode().value());
            response.getWriter().println("{\"message\":\"" + ex.getReason() + "\",\"status\":" + ex.getStatusCode().value() + ",\"path\":\"" + request.getRequestURI() + "\"}");
            response.getWriter().flush();
        } catch (org.springframework.security.core.AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"message\":\"" + ex.getMessage() + "\",\"status\":401,\"path\":\"" + request.getRequestURI() + "\"}");
            response.getWriter().flush();
        }
    }
}
