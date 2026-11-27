package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.repositories.EtudiantRepository;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    @Test
    void testAddEtudiantAvecMockito() {
        Etudiant etudiant = creerEtudiant("Ben Ali", "Salima");
        when(etudiantRepository.save(etudiant)).thenReturn(etudiant);

        Etudiant resultat = etudiantService.addEtudiant(etudiant);

        assertNotNull(resultat);
        assertEquals("Ben Ali", resultat.getNomEtudiant());
        verify(etudiantRepository).save(etudiant);
    }

    @Test
    void testGetAllEtudiantsAvecMockito() {
        List<Etudiant> liste = Arrays.asList(
                creerEtudiant("Ben Ali", "Salima"),
                creerEtudiant("Trabelsi", "Ahmed"));
        when(etudiantRepository.findAll()).thenReturn(liste);

        List<Etudiant> resultat = etudiantService.getAllEtudiants();

        assertEquals(2, resultat.size());
        verify(etudiantRepository).findAll();
    }

    @Test
    void testGetEtudiantByIdAvecMockito() {
        Etudiant etudiant = creerEtudiant("Ben Ali", "Salima");
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        Etudiant resultat = etudiantService.getEtudiantById(1L);

        assertNotNull(resultat);
        assertEquals("Salima", resultat.getPrenomEtudiant());
        verify(etudiantRepository).findById(1L);
    }

    @Test
    void testGetEtudiantByIdInexistantAvecMockito() {
        when(etudiantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> etudiantService.getEtudiantById(999L));
        verify(etudiantRepository).findById(999L);
    }

    @Test
    void testDeleteEtudiantAvecMockito() {
        when(etudiantRepository.existsById(1L)).thenReturn(true);

        etudiantService.deleteEtudiant(1L);

        verify(etudiantRepository).deleteById(1L);
    }

    @Test
    void testUpdateEtudiantAvecMockito() {
        Etudiant etudiant = creerEtudiant("Ben Ali Modifie", "Salima");
        when(etudiantRepository.save(etudiant)).thenReturn(etudiant);

        Etudiant resultat = etudiantService.updateEtudiant(etudiant);

        assertEquals("Ben Ali Modifie", resultat.getNomEtudiant());
        verify(etudiantRepository).save(etudiant);
    }

    private Etudiant creerEtudiant(String nom, String prenom) {
        return Etudiant.builder()
                .nomEtudiant(nom)
                .prenomEtudiant(prenom)
                .cinEtudiant(12345678L)
                .build();
    }
}