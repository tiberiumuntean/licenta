package utcn.licenta.server.responses;

import lombok.*;
import utcn.licenta.server.models.Club;

import java.util.Date;
import java.util.Set;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private int id;
    private int idRole;
    private String email;
    private String password;
    private Date registrationDate;
    private Set<Club> clubs;
}
