package tn.esprit.reservationservice.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.reservationservice.entities.Bloc;

@Repository
public interface BlocRepository extends CrudRepository<Bloc, Long> {
}