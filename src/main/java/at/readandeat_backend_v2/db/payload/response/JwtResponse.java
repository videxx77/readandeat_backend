package at.readandeat_backend_v2.db.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse
{
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String username;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String email, String username, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.username = username;
        this.roles = roles;
    }
}
