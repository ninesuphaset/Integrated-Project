package sit.int202.kp2itbmshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.kp2itbmshop.entities.SellerProfile;
import sit.int202.kp2itbmshop.entities.User;

import java.util.Optional;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Integer> {
    Optional<SellerProfile> findByUser(User user);
    Optional<SellerProfile> findByUserId(Integer userId);
}
