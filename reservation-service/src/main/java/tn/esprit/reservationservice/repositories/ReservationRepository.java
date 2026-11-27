package tn.esprit.reservationservice.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.reservationservice.entities.Reservation;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, String> {

    @Query("select r from Reservation r join Chambre c on r member of c.reservations where (c.bloc.idBloc = :idBloc and year(r.anneeUniversitaire) = year(current_date) and r.estValide = true) order by r.idReservation limit 1")
    Reservation findForReservation(long idBloc);

    @Query("select r from Reservation r join r.cinEtudiants cin where cin = :cinEtudiant and year(r.anneeUniversitaire) = :anneeUniversitaire")
    Reservation findByCinEtudiantAndAnneeUniversitaire(long cinEtudiant, int anneeUniversitaire);
}