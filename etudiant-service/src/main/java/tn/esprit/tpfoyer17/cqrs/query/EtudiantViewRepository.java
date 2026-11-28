package tn.esprit.tpfoyer17.cqrs.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EtudiantViewRepository extends JpaRepository<EtudiantView, String> {
}