package sit.int202.kp2itbmshop.entities;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class AuthUserDetail extends org.springframework.security.core.userdetails.User {
    private Integer id;

    public AuthUserDetail(Integer id, String username, String password) {
        this(id, username, password, new ArrayList<GrantedAuthority>());
    }

    public AuthUserDetail(Integer id, String username, String password
            , Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }
}
