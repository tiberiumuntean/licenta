package utcn.licenta.server.models.DTO.BasicDTO;

import lombok.*;

import java.sql.Timestamp;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDTO {
    private int id;
    private int role;
    private String email;
    private String password;
    private String image;
    private Timestamp registrationDate;

    private TrainerBasicDTO trainer;
    private ClientBasicDTO client;
}
