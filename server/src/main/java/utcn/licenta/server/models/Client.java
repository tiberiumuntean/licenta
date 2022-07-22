package utcn.licenta.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Date birthday;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Timestamp registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "client")
    private Set<Review> reviews;

    @OneToMany(mappedBy = "client")
    private Set<Invoice> invoices;

    @OneToMany(mappedBy = "client")
    private Set<Appointment> appointments;

    @ManyToMany(targetEntity = FitnessClass.class, mappedBy = "clients")
    private Set<FitnessClass> fitnessClasses;

    @OneToMany(mappedBy = "client")
    private Set<ClientMembership> clientMemberships;

    @Column(insertable = false, updatable = false)
    private Integer trainer_id;

    @Column(insertable = false, updatable = false)
    private Integer user_id;

    public void addClass(FitnessClass fitnessClass){
        this.fitnessClasses.add(fitnessClass);
    }
}
