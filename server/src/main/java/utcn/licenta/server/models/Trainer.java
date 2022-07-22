package utcn.licenta.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private int freeSpots;
    private int workExperience;
    private String description;
    private String instagram;
    private String facebook;
    private double price;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Timestamp registrationDate;

    @OneToMany(mappedBy = "trainer")
    private Set<Client> clients;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "trainer")
    private Set<Review> reviews;

    @OneToMany(mappedBy = "trainer")
    private Set<Appointment> appointments;

    @OneToMany(mappedBy = "trainer")
    private Set<FitnessClass> fitnessClasses;

    @OneToMany(mappedBy = "trainer")
    private Set<Invoice> invoices;

    @Column(insertable = false, updatable = false)
    private Integer user_id;
}
