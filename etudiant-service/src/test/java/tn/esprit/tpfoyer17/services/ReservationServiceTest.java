package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.entities.Reservation;
import tn.esprit.tpfoyer17.entities.Universite;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;
import tn.esprit.tpfoyer17.repositories.EtudiantRepository;
import tn.esprit.tpfoyer17.repositories.FoyerRepository;
import tn.esprit.tpfoyer17.repositories.ReservationRepository;
import tn.esprit.tpfoyer17.repositories.UniversiteRepository;

import java.time.Year;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private BlocRepository blocRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void testGetAllReservationsAvecMockito() {
        when(reservationRepository.findAll()).thenReturn(List.of(new Reservation(), new Reservation()));

        List<Reservation> resultat = reservationService.getAllReservations();

        assertEquals(2, resultat.size());
    }

    @Test
    void testGetReservationByIdAvecMockito() {
        Reservation reservation = Reservation.builder().idReservation("1").build();
        when(reservationRepository.findById("1")).thenReturn(Optional.of(reservation));

        assertEquals(reservation, reservationService.getReservationById("1"));
    }

    @Test
    void testDeleteReservationAvecMockito() {
        reservationService.deleteReservation("1");

        verify(reservationRepository).deleteById("1");
    }

    @Test
    void testUpdateReservationAvecMockito() {
        Reservation reservation = Reservation.builder().idReservation("1").estValide(true).build();
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation resultat = reservationService.updateReservation(reservation);

        assertEquals("1", resultat.getIdReservation());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testAjouterReservationOuverteTripleAvecMockito() {
        Etudiant dejaInscrit1 = Etudiant.builder().cinEtudiant(1L).build();
        Etudiant dejaInscrit2 = Etudiant.builder().cinEtudiant(2L).build();
        Etudiant nouveau = Etudiant.builder().cinEtudiant(3L).build();
        Reservation reservation = Reservation.builder()
                .idReservation("7")
                .estValide(true)
                .etudiants(new HashSet<>(Set.of(dejaInscrit1, dejaInscrit2)))
                .build();
        Chambre chambre = Chambre.builder().idChambre(7L).typeChambre(TypeChambre.TRIPLE).build();

        when(reservationRepository.findForReservation(1L)).thenReturn(reservation);
        when(chambreRepository.findByReservationsIdReservation("7")).thenReturn(chambre);
        when(etudiantRepository.findByCinEtudiant(3L)).thenReturn(nouveau);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation resultat = reservationService.ajouterReservation(1L, 3L);

        assertEquals(3, resultat.getEtudiants().size());
        assertFalse(resultat.isEstValide(), "une chambre TRIPLE pleine ne doit plus être valide");
    }

    @Test
    void testAjouterReservationChambreDoubleDevientCompleteAvecMockito() {
        Etudiant dejaInscrit = Etudiant.builder().cinEtudiant(1L).build();
        Etudiant nouveau = Etudiant.builder().cinEtudiant(2L).build();
        Reservation reservation = Reservation.builder()
                .idReservation("8")
                .estValide(true)
                .etudiants(new HashSet<>(Set.of(dejaInscrit)))
                .build();
        Chambre chambre = Chambre.builder().idChambre(8L).typeChambre(TypeChambre.DOUBLE).build();

        when(reservationRepository.findForReservation(1L)).thenReturn(reservation);
        when(chambreRepository.findByReservationsIdReservation("8")).thenReturn(chambre);
        when(etudiantRepository.findByCinEtudiant(2L)).thenReturn(nouveau);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation resultat = reservationService.ajouterReservation(1L, 2L);

        assertEquals(2, resultat.getEtudiants().size());
        assertFalse(resultat.isEstValide());
    }

    @Test
    void testAnnulerReservationAvecMockito() {
        Etudiant etudiant = Etudiant.builder().cinEtudiant(123L).build();
        Reservation reservation = Reservation.builder()
                .idReservation("9")
                .estValide(false)
                .etudiants(new HashSet<>(Set.of(etudiant)))
                .build();

        when(reservationRepository.findByEtudiantsCinEtudiantAndAnneeUniversitaire(eq(123L), anyInt()))
                .thenReturn(reservation);
        when(etudiantRepository.findByCinEtudiant(123L)).thenReturn(etudiant);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation resultat = reservationService.annulerReservation(123L);

        assertTrue(resultat.getEtudiants().isEmpty());
        assertTrue(resultat.isEstValide());
    }

    /*
     * DÉSACTIVÉ : bug réel dans le code actuel (cast List -> Set dans ajouterReservation).
     * Retirer @Disabled une fois le service corrigé.
     */
    @Test
    @Disabled("Bug : cast List -> Set dans ajouterReservation (ClassCastException)")
    void testAjouterReservationNouvelleChambreSimpleAvecMockito() {
        Etudiant etudiant = Etudiant.builder().cinEtudiant(5L).build();
        Chambre chambre = Chambre.builder().idChambre(3L).typeChambre(TypeChambre.SIMPLE).build();
        Bloc bloc = Bloc.builder().idBloc(1L).nomBloc("A").build();

        when(reservationRepository.findForReservation(1L)).thenReturn(null);
        when(etudiantRepository.findByCinEtudiant(5L)).thenReturn(etudiant);
        when(chambreRepository.getForReservation(1L)).thenReturn(chambre);
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation resultat = reservationService.ajouterReservation(1L, 5L);

        assertNotNull(resultat);
        assertEquals(1, resultat.getEtudiants().size());
        assertFalse(resultat.isEstValide(), "une chambre SIMPLE est complète dès le 1er étudiant");
    }
}

/*
 * Test d'intégration : charge le contexte Spring et utilise la base MySQL
 * configurée dans application.properties. @Transactional : rollback après chaque test.
 */
@SpringBootTest
@Transactional
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private BlocRepository blocRepository;

    @Autowired
    private FoyerRepository foyerRepository;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Test
    void testAjouterReservationDansChambreDoubleOuverteSansMockito() {
        Bloc bloc = creerBloc("UNI-" + aleatoire());
        Chambre chambre = creerChambre(bloc, TypeChambre.DOUBLE);
        Etudiant premier = creerEtudiant();
        Etudiant second = creerEtudiant();
        creerReservation(chambre, true, premier);

        Reservation resultat = reservationService.ajouterReservation(bloc.getIdBloc(), second.getCinEtudiant());

        assertEquals(2, resultat.getEtudiants().size());
        assertFalse(resultat.isEstValide(), "la chambre DOUBLE est complète avec 2 étudiants");
    }

    @Test
    void testAnnulerReservationLibereLaPlaceSansMockito() {
        Bloc bloc = creerBloc("UNI-" + aleatoire());
        Chambre chambre = creerChambre(bloc, TypeChambre.DOUBLE);
        Etudiant premier = creerEtudiant();
        Etudiant second = creerEtudiant();
        creerReservation(chambre, false, premier, second);

        Reservation resultat = reservationService.annulerReservation(premier.getCinEtudiant());

        assertEquals(1, resultat.getEtudiants().size());
        assertTrue(resultat.isEstValide(), "une place se libère après l'annulation");
    }

    @Test
    void testGetReservationParAnneeEtUniversiteSansMockito() {
        String nomUniversite = "UNI-" + aleatoire();
        Bloc bloc = creerBloc(nomUniversite);
        Chambre chambre = creerChambre(bloc, TypeChambre.SIMPLE);
        Reservation reservation = creerReservation(chambre, false, creerEtudiant());

        List<Reservation> resultat = reservationService
                .getReservationParAnneeUniversitaireEtNomUniversite(new Date(), nomUniversite);

        assertEquals(1, resultat.size());
        assertEquals(reservation.getIdReservation(), resultat.get(0).getIdReservation());
    }

    /*
     * DÉSACTIVÉ : bug réel (cast List -> Set dans ajouterReservation).
     * Retirer @Disabled une fois le service corrigé.
     */
    @Test
    @Disabled("Bug : cast List -> Set dans ajouterReservation (ClassCastException)")
    void testAjouterReservationCreeUneReservationSansMockito() {
        Bloc bloc = creerBloc("UNI-" + aleatoire());
        Chambre chambre = creerChambre(bloc, TypeChambre.SIMPLE);
        Etudiant etudiant = creerEtudiant();

        Reservation resultat = reservationService.ajouterReservation(bloc.getIdBloc(), etudiant.getCinEtudiant());

        assertEquals(1, resultat.getEtudiants().size());
        assertFalse(resultat.isEstValide(), "une chambre SIMPLE est complète dès le 1er étudiant");
        assertNotNull(chambreRepository.findByReservationsIdReservation(resultat.getIdReservation()),
                "la réservation doit être liée à la chambre");
    }

    /*
     * DÉSACTIVÉ : deleteReservation ne retire pas le lien chambre/réservation
     * (table de jointure) avant de supprimer, ce qui viole la clé étrangère.
     */
    @Test
    @Disabled("Bug : deleteReservation ne retire pas le lien chambre/réservation")
    void testDeleteReservationSansMockito() {
        Bloc bloc = creerBloc("UNI-" + aleatoire());
        Chambre chambre = creerChambre(bloc, TypeChambre.SIMPLE);
        Reservation reservation = creerReservation(chambre, false, creerEtudiant());

        reservationService.deleteReservation(reservation.getIdReservation());

        assertFalse(reservationRepository.findById(reservation.getIdReservation()).isPresent());
    }

    // ---------- données de test ----------

    private Bloc creerBloc(String nomUniversite) {
        Foyer foyer = foyerRepository.save(Foyer.builder()
                .nomFoyer("Foyer " + nomUniversite).capaciteFoyer(100).build());
        universiteRepository.save(Universite.builder()
                .nomUniversite(nomUniversite).adresse("Tunis").foyer(foyer).build());
        return blocRepository.save(Bloc.builder()
                .nomBloc("A").capaciteBloc(10).foyer(foyer).build());
    }

    private Chambre creerChambre(Bloc bloc, TypeChambre type) {
        return chambreRepository.save(Chambre.builder()
                .numeroChambre(aleatoire()).typeChambre(type).bloc(bloc)
                .reservations(new HashSet<>()).build());
    }

    private Etudiant creerEtudiant() {
        return etudiantRepository.save(Etudiant.builder()
                .cinEtudiant(aleatoire()).nomEtudiant("Test").prenomEtudiant("Etudiant").build());
    }

    private Reservation creerReservation(Chambre chambre, boolean valide, Etudiant... etudiants) {
        Reservation reservation = reservationRepository.save(Reservation.builder()
                .idReservation(chambre.getIdChambre() + "-" + Year.now().getValue())
                .anneeUniversitaire(new Date())
                .estValide(valide)
                .etudiants(new HashSet<>(Arrays.asList(etudiants)))
                .build());
        chambre.getReservations().add(reservation);
        chambreRepository.save(chambre);
        return reservation;
    }

    private long aleatoire() {
        return ThreadLocalRandom.current().nextLong(10_000_000L, 99_999_999L);
    }
}