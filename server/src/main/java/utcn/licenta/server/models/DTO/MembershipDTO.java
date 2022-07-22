package utcn.licenta.server.models.DTO;

import lombok.*;
import utcn.licenta.server.models.DTO.BasicDTO.ClubBasicDTO;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDTO {
    private int id;
    private String name;
    private Double price;
    private int duration;
    private ClubBasicDTO club;
}
