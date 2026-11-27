package tn.esprit.tpfoyer17.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.repositories.EtudiantRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EtudiantService implements IEtudiantService{
    EtudiantRepository etudiantRepository;

    @Override
    public Etudiant addEtudiant(Etudiant etudiant) {
        log.info("Ajout d'un nouvel étudiant");
        Etudiant saved = etudiantRepository.save(etudiant);
        log.debug("Étudiant ajouté avec l'id {}", saved.getIdEtudiant());
        return saved;
    }

    @Override
    public List<Etudiant> getAllEtudiants() {
        log.info("Récupération de tous les étudiants");
        List<Etudiant> etudiants = (List<Etudiant>) etudiantRepository.findAll();
        log.debug("Nombre d'étudiants récupérés : {}", etudiants.size());
        return etudiants;
    }

    @Override
    public Etudiant getEtudiantById(long idEtudiant) {
        log.debug("Recherche de l'étudiant avec l'id {}", idEtudiant);
        return etudiantRepository.findById(idEtudiant).orElseThrow(() -> {
            log.error("Étudiant introuvable avec l'id {}", idEtudiant);
            return new NoSuchElementException("Étudiant introuvable avec l'id " + idEtudiant);
        });
    }

    @Override
    public void deleteEtudiant(long idEtudiant) {
        if (!etudiantRepository.existsById(idEtudiant)) {
            log.warn("Suppression demandée pour un étudiant inexistant, id {}", idEtudiant);
        }
        etudiantRepository.deleteById(idEtudiant);
        log.info("Suppression de l'étudiant avec l'id {}", idEtudiant);
    }

    @Override
    public Etudiant updateEtudiant(Etudiant etudiant) {
        log.info("Mise à jour de l'étudiant avec l'id {}", etudiant.getIdEtudiant());
        return etudiantRepository.save(etudiant);
    }
}
