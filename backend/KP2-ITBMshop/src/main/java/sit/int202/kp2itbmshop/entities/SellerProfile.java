package sit.int202.kp2itbmshop.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "seller_profiles")
public class SellerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id", nullable = false)
    private Integer seller_id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "phoneNumber", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "bankAccount", nullable = false, length = 50)
    private String bankAccount;

    @Column(name = "bankName", nullable = false, length = 100)
    private String bankName;

    @Column(name = "idCardNumber", nullable = false, length = 50)
    private String idCardNumber;

    @Column(name = "idCardImageFront", nullable = false)
    private String idCardImageFront;

    @Column(name = "idCardImageBack", nullable = false)
    private String idCardImageBack;

}