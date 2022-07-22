package utcn.licenta.server.models;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Double price;
    private int duration;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "membership")
    private Set<ClientMembership> clientMemberships;

    @OneToMany(mappedBy = "membership")
    private Set<Invoice> invoices;

    @Column(insertable = false, updatable = false)
    private Integer club_id;
}