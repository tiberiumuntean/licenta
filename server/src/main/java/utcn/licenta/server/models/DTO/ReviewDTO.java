package utcn.licenta.server.models.DTO;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private int id;
    private int rating;
    private String review;
    private String creationDate;
    private ClientDTO client;
}
