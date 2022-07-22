package utcn.licenta.server.models.DTO.BasicDTO;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipBasicDTO {
    private int id;
    private String name;
    private Double price;
    private int duration;
    private Integer club_id;
}
