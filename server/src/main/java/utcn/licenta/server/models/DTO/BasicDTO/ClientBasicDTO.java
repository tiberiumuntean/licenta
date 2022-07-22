package utcn.licenta.server.models.DTO.BasicDTO;

import lombok.*;
import utcn.licenta.server.models.DTO.ClientMembershipDTO;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientBasicDTO {
    private int id;
    private int age;
    private String image;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Timestamp registrationDate;

    private Set<ClientMembershipDTO> memberships;

    private String birthday;
    private Integer trainer_id;
    private Integer user_id;
}
