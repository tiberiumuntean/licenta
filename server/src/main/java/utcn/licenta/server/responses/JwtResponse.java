package utcn.licenta.server.responses;

import lombok.Data;
import utcn.licenta.server.models.Club;

import java.util.Set;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private int id;
    private String email;
    private Set<Club> clubs;

    private int role;

    public JwtResponse(String accessToken, int id, String email, Set<Club> clubs, int role) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.clubs = clubs;
        this.role = role;
    }
}