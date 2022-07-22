package utcn.licenta.server.models.DTO;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private int id;
    private String name;
    private String date;

    private TrainerDTO trainer;
    private ClientDTO client;
}
