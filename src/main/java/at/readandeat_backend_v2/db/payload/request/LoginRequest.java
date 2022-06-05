package at.readandeat_backend_v2.db.payload.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest
{
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
