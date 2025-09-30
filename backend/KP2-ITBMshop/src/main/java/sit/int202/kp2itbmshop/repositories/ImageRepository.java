package sit.int202.kp2itbmshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.kp2itbmshop.entities.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findBySaleItemIdOrderByPositionAsc(Integer saleItemId);
}
