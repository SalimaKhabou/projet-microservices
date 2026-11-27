package tn.esprit.reservationservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.reservationservice.entities.enumerations.TypeChambre;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chambre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    long idChambre;

    long numeroChambre;

    @Enumerated(EnumType.STRING)
    TypeChambre typeChambre;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    Bloc bloc;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany
    Set<Reservation> reservations;
}