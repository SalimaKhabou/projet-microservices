package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.tpfoyer17.entities.Etudiant;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EtudiantServiceIntegrationTest {

    @Autowired
    private IEtudiantService etudiantService;

    @Test
    void testAddEtudiantSansMockito() {
        Etudiant etudiant = etudiantService.addEtudiant(
                creerEtudiant("Integration Add", "Salima"));

        assertTrue(etudiant.getIdEtudiant() > 0);
        etudiantService.deleteEtudiant(etudiant.getIdEtudiant());
    }

    @Test
    void testGetEtudiantByIdSansMockito() {
        Etudiant etudiant = etudiantService.addEtudiant(
                creerEtudiant("Integration Get", "Salima"));

        Etudiant resultat = etudiantService.getEtudiantById(etudiant.getIdEtudiant());

        assertNotNull(resultat);
        assertEquals(etudiant.getIdEtudiant(), resultat.getIdEtudiant());
        assertEquals("Integration Get", resultat.getNomEtudiant());
        etudiantService.deleteEtudiant(etudiant.getIdEtudiant());
    }

    @Test
    void testUpdateEtudiantSansMockito() {
        Etudiant etudiant = etudiantService.addEtudiant(
                creerEtudiant("Avant modification", "Salima"));
        etudiant.setNomEtudiant("Apres modification");

        Etudiant resultat = etudiantService.updateEtudiant(etudiant);

        assertEquals("Apres modification", resultat.getNomEtudiant());
        etudiantService.deleteEtudiant(etudiant.getIdEtudiant());
    }

    @Test
    void testDeleteEtudiantSansMockito() {
        Etudiant etudiant = etudiantService.addEtudiant(
                creerEtudiant("Integration Delete", "Salima"));

        long id = etudiant.getIdEtudiant();
        etudiantService.deleteEtudiant(id);

        assertThrows(NoSuchElementException.class,
                () -> etudiantService.getEtudiantById(id));
    }

    private Etudiant creerEtudiant(String nom, String prenom) {
        return Etudiant.builder()
                .nomEtudiant(nom)
                .prenomEtudiant(prenom)
                .cinEtudiant(12345678L)
                .build();
    }
}