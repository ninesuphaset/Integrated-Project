package sit.int202.kp2itbmshop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sit.int202.kp2itbmshop.entities.AuthUserDetail;
import sit.int202.kp2itbmshop.entities.User;
import sit.int202.kp2itbmshop.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class JwtUserDetailsService  implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new AuthUserDetail(user.getId(), user.getEmail()
                , user.getPassword(), getAuthorities(user.getUserType()));
    }

    public UserDetails loadUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User id " + id + " does not exist"));
        return new AuthUserDetail(user.getId(), user.getEmail()
                , user.getPassword(), getAuthorities(user.getUserType()));
    }

    public static List<GrantedAuthority> getAuthorities(String rolesAsCommaSeparated) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Arrays.asList(rolesAsCommaSeparated.split(","))
                .forEach(role -> authorities.add(getAuthority(role)));
        return authorities;
    }

    private static GrantedAuthority getAuthority(String role) {
        return new SimpleGrantedAuthority(role);
    }
}

