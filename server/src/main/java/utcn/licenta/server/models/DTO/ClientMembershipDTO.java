package utcn.licenta.server.models.DTO;

import lombok.*;
import utcn.licenta.server.models.DTO.BasicDTO.MembershipBasicDTO;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientMembershipDTO {
    private int id;
    // private ClientDTO client;
    private MembershipBasicDTO membership;
    private String membershipStartDate;
    private String membershipEndDate;
}
