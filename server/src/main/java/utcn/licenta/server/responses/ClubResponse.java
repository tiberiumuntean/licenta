package utcn.licenta.server.responses;

import lombok.*;
import utcn.licenta.server.models.User;

import java.util.Set;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClubResponse {
    private int id;
    private String name;
    private String address;
    private int phoneNumber;
    private String email;
    private String schedule;
    private double latitude;
    private double longitude;
    private Set<User> users;
}
