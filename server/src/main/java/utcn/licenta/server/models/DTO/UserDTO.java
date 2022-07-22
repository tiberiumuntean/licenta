package utcn.licenta.server.models.DTO;

import lombok.*;
import utcn.licenta.server.models.DTO.BasicDTO.ClubBasicDTO;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int id;
    private int role;
    private String email;
    private String password;
    private String image;
    private Timestamp registrationDate;

    private Set<ClubBasicDTO> clubs;
    private TrainerDTO trainer;
    private ClientDTO client;
}
