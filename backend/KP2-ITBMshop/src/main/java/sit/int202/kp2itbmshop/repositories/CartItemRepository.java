package sit.int202.kp2itbmshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.kp2itbmshop.entities.CartItem;
import sit.int202.kp2itbmshop.entities.User;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndSaleitemId(User user, Integer saleitemId);


}