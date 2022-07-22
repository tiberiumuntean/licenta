package utcn.licenta.server.models.DTO;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private int id;
    private int number;
    private String series;
    private String date;

    private ClubDTO club;
    private ClientDTO client;
    private TrainerDTO trainer;
    private MembershipDTO membership;
}
