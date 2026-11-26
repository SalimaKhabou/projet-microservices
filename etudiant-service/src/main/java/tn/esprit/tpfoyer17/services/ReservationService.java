package tn.esprit.tpfoyer17.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.entities.Reservation;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;
import tn.esprit.tpfoyer17.repositories.EtudiantRepository;
import tn.esprit.tpfoyer17.repositories.ReservationRepository;

import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationService implements IReservationService{
    ReservationRepository reservationRepository;
    EtudiantRepository etudiantRepository;
    ChambreRepository chambreRepository;
    BlocRepository blocRepository;



    @Override
    public List<Reservation> getAllReservations() {
        return (List<Reservation>) reservationRepository.findAll();
    }

    @Override
    public Reservation getReservationById(String idReservation) {
        return reservationRepository.findById(idReservation).orElseThrow();
    }

    @Override
    public void deleteReservation(String idReservation) {
        reservationRepository.deleteById(idReservation);

    }

    @Override
    public Reservation updateReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation ajouterReservation(long idBloc, long cinEtudiant) {
        Etudiant etudiant = etudiantRepository.findByCinEtudiant(cinEtudiant);
        Reservation reservation = reservationRepository.findForReservation(idBloc);

        if (reservation != null) {
            reservation.getEtudiants().add(etudiant);
            Chambre chambre = chambreRepository.findByReservationsIdReservation(reservation.getIdReservation());
            TypeChambre type = chambre.getTypeChambre();
            if (type == TypeChambre.DOUBLE
                    || (type == TypeChambre.TRIPLE && reservation.getEtudiants().size() == 3)) {
                reservation.setEstValide(false);
            }
            return reservationRepository.save(reservation);
        }

        Set<Etudiant> etudiants = new HashSet<>();
        etudiants.add(etudiant);
        Chambre chambre = chambreRepository.getForReservation(idBloc);
        Bloc bloc = blocRepository.findById(idBloc).orElseThrow();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Reservation nouvelle = Reservation.builder()
                .idReservation(chambre.getIdChambre() + bloc.getNomBloc() + calendar.get(Calendar.YEAR))
                .anneeUniversitaire(new Date())
                .etudiants(etudiants)
                .estValide(chambre.getTypeChambre() != TypeChambre.SIMPLE)
                .build();
        return reservationRepository.save(nouvelle);
    }

    @Override
    public Reservation annulerReservation(long cinEtudiant) {
        Reservation reservation = reservationRepository.findByEtudiantsCinEtudiantAndAnneeUniversitaire(cinEtudiant, Year.now().getValue());
        Etudiant etudiant = etudiantRepository.findByCinEtudiant(cinEtudiant);
        reservation.getEtudiants().remove(etudiant);
        reservation.setEstValide(true);
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getReservationParAnneeUniversitaireEtNomUniversite(Date anneeUniversite, String nomUniversite) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(anneeUniversite);
        return reservationRepository.findByAnneeUniversitaireYearAndNomUniversite(calendar.get(Calendar.YEAR), nomUniversite);

    }
}
