package tn.esprit.reservationservice.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reservation {

    @Id
    String idReservation;

    Date anneeUniversitaire;

    boolean estValide;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reservation_etudiants",
                     joinColumns = @JoinColumn(name = "id_reservation"))
    @Column(name = "cin_etudiant")
    @Builder.Default
    Set<Long> cinEtudiants = new HashSet<>();
}