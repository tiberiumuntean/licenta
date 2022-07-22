package utcn.licenta.server.models.stripe;

import lombok.Data;

@Data
public class CheckoutProduct {
    private String name;
    private Long price;
}
