package utcn.licenta.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Member;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String schedule;
    private double latitude;
    private double longitude;

    @OneToMany(mappedBy = "club")
    private Set<Invoice> invoices;

    @OneToMany(mappedBy = "club")
    private Set<Membership> memberships;

    @ManyToMany(targetEntity = User.class, mappedBy = "clubs")
    private Set<User> users;

    @OneToMany(mappedBy = "club")
    private Set<FitnessClass> fitnessClasses;

    public void addFitnessClass(FitnessClass fitnessClass){
        this.fitnessClasses.add(fitnessClass);
    }
}
