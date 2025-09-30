package sit.int202.kp2itbmshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saleitem_id", nullable = false)
    @JsonIgnore
    private SaleItem saleItem;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "file_name", nullable = false)
    private String fileName;
}
