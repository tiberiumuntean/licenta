package utcn.licenta.server.models.DTO;

import lombok.*;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private int freeSpots;
    private int workExperience;
    private String description;
    private String instagram;
    private String facebook;
    private Timestamp registrationDate;
    private double price;

    private Set<ClientDTO> clients;
    private Set<ReviewDTO> reviews;

    private Integer user_id;
}
