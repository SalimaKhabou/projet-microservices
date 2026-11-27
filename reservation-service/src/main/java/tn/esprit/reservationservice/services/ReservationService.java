package tn.esprit.reservationservice.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.reservationservice.clients.EtudiantClient;
import tn.esprit.reservationservice.dto.EtudiantDto;
import tn.esprit.reservationservice.entities.Bloc;
import tn.esprit.reservationservice.entities.Chambre;
import tn.esprit.reservationservice.entities.Reservation;
import tn.esprit.reservationservice.entities.enumerations.TypeChambre;
import tn.esprit.reservationservice.repositories.BlocRepository;
import tn.esprit.reservationservice.repositories.ChambreRepository;
import tn.esprit.reservationservice.repositories.ReservationRepository;

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
public class ReservationService implements IReservationService {
    ReservationRepository reservationRepository;
    ChambreRepository chambreRepository;
    BlocRepository blocRepository;
    EtudiantClient etudiantClient;

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
        // Appel REST vers etudiant-service : vérifie que l'étudiant existe
        EtudiantDto etudiant = etudiantClient.getByCin(cinEtudiant);
        log.info("Étudiant trouvé : {} {}", etudiant.prenomEtudiant(), etudiant.nomEtudiant());

        Reservation reservation = reservationRepository.findForReservation(idBloc);

        if (reservation != null) {
            reservation.getCinEtudiants().add(cinEtudiant);
            Chambre chambre = chambreRepository.findByReservationsIdReservation(reservation.getIdReservation());
            TypeChambre type = chambre.getTypeChambre();
            if (type == TypeChambre.DOUBLE
                    || (type == TypeChambre.TRIPLE && reservation.getCinEtudiants().size() == 3)) {
                reservation.setEstValide(false);
            }
            return reservationRepository.save(reservation);
        }

        Set<Long> cins = new HashSet<>();
        cins.add(cinEtudiant);
        Chambre chambre = chambreRepository.getForReservation(idBloc);
        Bloc bloc = blocRepository.findById(idBloc).orElseThrow();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Reservation nouvelle = Reservation.builder()
                .idReservation(chambre.getIdChambre() + bloc.getNomBloc() + calendar.get(Calendar.YEAR))
                .anneeUniversitaire(new Date())
                .cinEtudiants(cins)
                .estValide(chambre.getTypeChambre() != TypeChambre.SIMPLE)
                .build();
        Reservation saved = reservationRepository.save(nouvelle);

        // Lien chambre -> réservation (absent du code d'origine, nécessaire pour retrouver la réservation ensuite)
        chambre.getReservations().add(saved);
        chambreRepository.save(chambre);
        return saved;
    }

    @Override
    public Reservation annulerReservation(long cinEtudiant) {
        Reservation reservation = reservationRepository
                .findByCinEtudiantAndAnneeUniversitaire(cinEtudiant, Year.now().getValue());
        reservation.getCinEtudiants().remove(cinEtudiant);
        reservation.setEstValide(true);
        return reservationRepository.save(reservation);
    }
}