package sit.int202.kp2itbmshop.repositories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int202.kp2itbmshop.entities.SaleItem;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem,Integer> , JpaSpecificationExecutor<SaleItem> {
    List<SaleItem> findAllByOrderByCreatedOnAscIdAsc();
    @Query("SELECT COUNT(s) FROM SaleItem s WHERE s.brand.id = :brandId")
    Integer countSaleitemByBrandId(@Param("brandId") Integer brandId);

    @Query("SELECT s FROM SaleItem s WHERE s.seller.user.id = :userId")
    List<SaleItem> findBySeller_User_Id(@Param("userId") Integer userId);

    Page<SaleItem> findBySeller_User_Id(@Param("userId") Integer userId, Pageable pageable);


}




