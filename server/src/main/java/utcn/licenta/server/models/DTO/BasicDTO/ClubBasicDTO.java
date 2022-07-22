package utcn.licenta.server.models.DTO.BasicDTO;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubBasicDTO {
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String schedule;
    private double latitude;
    private double longitude;
}
