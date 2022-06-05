package at.readandeat_backend_v2.db.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

//entity Klasse für Rollen (keine Verwendung im Moment im Projekt; vielleicht irgendwann)
@Entity
@Table(name = "Role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID")
    private long roleID;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, name="Name")
    private ERole name;
}
