package at.readandeat_backend_v2.db.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private long productID;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "Price", nullable = false)
    private Double price;

    @Column(name = "PictureURL")
    private String pictureURL;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private User user;
}
