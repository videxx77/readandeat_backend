package at.readandeat_backend_v2.db.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest
{
    private String name;

    private String pictureURL;

    private double price;
}
