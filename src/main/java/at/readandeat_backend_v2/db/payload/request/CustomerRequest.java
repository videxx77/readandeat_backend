package at.readandeat_backend_v2.db.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest
{
    private double balance;

    private String firstName;

    private String lastName;

    private MultipartFile image;

    private long accountID;
}
