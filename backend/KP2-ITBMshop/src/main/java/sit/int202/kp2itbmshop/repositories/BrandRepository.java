package sit.int202.kp2itbmshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.kp2itbmshop.entities.Brand;

import java.util.List;


public interface BrandRepository extends JpaRepository<Brand, Integer> {
    List<Brand> findAllByIsActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
