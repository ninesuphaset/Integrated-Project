package sit.int202.kp2itbmshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.kp2itbmshop.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
}
