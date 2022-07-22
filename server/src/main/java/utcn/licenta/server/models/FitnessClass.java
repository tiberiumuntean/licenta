package utcn.licenta.server.models;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FitnessClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private String location;
    private Integer freeSpots;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToMany(targetEntity = Client.class, cascade = CascadeType.REFRESH)
    @JoinTable(name = "client_class", joinColumns = @JoinColumn(name = "class_id"), inverseJoinColumns = @JoinColumn(name = "client_id"))
    private Set<Client> clients;

    private Date date;

    @Column(insertable = false, updatable = false)
    private Integer club_id;

    public void addClient(Client client){
        this.clients.add(client);
    }
}
