package at.readandeat_backend_v2.db.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Customer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID")
    private long customerID;

    @Column(name = "FirstName", length = 50, nullable = false)
    private String firstName;

    @Column(name = "LastName", length = 50)
    private String lastName;

    @Column(name = "Balance")
    private Double balance;

    @Column(name = "PictureURL")
    private String pictureURL;

    //ToDo: add nfc field

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private User user;

    public Customer(String firstName, String lastName, Double balance)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

}
