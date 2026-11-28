package tn.esprit.tpfoyer17.cqrs.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "etudiant_view")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantView {
    @Id
    private String id;
    private String nom;
    private String prenom;
    private long cin;
}