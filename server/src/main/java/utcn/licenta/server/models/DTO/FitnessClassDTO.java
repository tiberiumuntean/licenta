package utcn.licenta.server.models.DTO;

import lombok.*;
import utcn.licenta.server.models.DTO.BasicDTO.ClientBasicDTO;
import utcn.licenta.server.models.DTO.BasicDTO.TrainerBasicDTO;

import java.util.Set;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FitnessClassDTO {
    private int id;
    private String name;
    private String description;
    private String location;
    private Integer freeSpots;
    private String date;
    private TrainerBasicDTO trainer;
    // private ClubDTO club;
    private Set<ClientBasicDTO> clients;
    private int club_id;
}
