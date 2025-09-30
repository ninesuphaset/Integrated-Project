package sit.int202.kp2itbmshop.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "saleitem")
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brandId", nullable = false)
    private Brand brand;

    @Column(name = "model", nullable = false, length = 60)
    private String model;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "screenSizeInch", precision = 3, scale = 1)
    private BigDecimal screenSizeInch;

    @Column(name = "ramGb")
    private Integer ramGb;

    @Column(name = "storageGb")
    private Integer storageGb;

    @Column(name = "color", length = 50)
    private String color;

    @CreationTimestamp
    @Column(name = "createdOn", nullable = false, updatable = false)
    private Instant createdOn;

    @UpdateTimestamp
    @Column(name = "updatedOn", nullable = false)
    private Instant updatedOn;

    @OneToMany(mappedBy = "saleItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> saleItemImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    @JoinColumn(name = "sellerId", nullable = false, referencedColumnName = "user_id")

    private SellerProfile seller;

}