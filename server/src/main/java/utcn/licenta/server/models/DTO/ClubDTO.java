package utcn.licenta.server.models.DTO;

import lombok.*;
import utcn.licenta.server.models.DTO.BasicDTO.UserBasicDTO;

import java.util.Set;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubDTO {
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String schedule;
    private double latitude;
    private double longitude;
    private Set<FitnessClassDTO> fitnessClasses;
    private Set<UserBasicDTO> users;
}
