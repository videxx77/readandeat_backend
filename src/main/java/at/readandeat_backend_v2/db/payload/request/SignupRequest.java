package at.readandeat_backend_v2.db.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest
{
    @Email
    private String email;

    @Size(min = 3, max = 49)
    private String username;

    private String firstName;

    private String lastName;

    @Size(min = 8, max = 49)
    private String password;

    private String nfc;

    private Set<String> roles;
}
