package at.readandeat_backend_v2.db.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//entity Klasse für User (User legen ihre Kunden an)
@Entity
@Table(name = "Account") //nicht User wegen Datenbank Namenskonventionen
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private long userID;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Username", nullable = false, unique = true)
    private String username;

    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Column(name = "LastName", nullable = false)
    private String lastName;

    @Column(name = "Password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "Account_Role",
            joinColumns = @JoinColumn(name = "AccountID"),
            inverseJoinColumns = @JoinColumn(name = "RoleID"))
    private Set<Role> roles = new HashSet<>();


    public User(String email, String username, String firstName, String lastName, String password)
    {
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
}
