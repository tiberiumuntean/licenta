package utcn.licenta.server.models.stripe;

import lombok.Data;

@Data
public class ClientSecret {
    private String clientSecret;

    public ClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
