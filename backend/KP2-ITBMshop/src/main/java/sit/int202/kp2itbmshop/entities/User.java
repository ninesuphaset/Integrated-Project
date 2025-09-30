package sit.int202.kp2itbmshop.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Lob
    @Column(name = "userType", nullable = false)
    private String userType;

    @Column(name = "nickName", nullable = false, length = 50)
    private String nickName;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "fullName", nullable = false, length = 40)
    private String fullName;

    @ColumnDefault("0")
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = false;

    @CreationTimestamp
    @Column(name = "createdOn", nullable = false, updatable = false)
    private Instant createdOn;

    @UpdateTimestamp
    @Column(name = "updatedOn", nullable = false)
    private Instant updatedOn;
}