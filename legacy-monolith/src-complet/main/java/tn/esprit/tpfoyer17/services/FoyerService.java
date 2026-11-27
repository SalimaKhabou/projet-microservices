package tn.esprit.tpfoyer17.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.entities.Universite;
import tn.esprit.tpfoyer17.repositories.FoyerRepository;
import tn.esprit.tpfoyer17.repositories.UniversiteRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoyerService implements IFoyerService{
    FoyerRepository foyerRepository;
    UniversiteRepository universiteRepository;

    @Override
    public Foyer addFoyer(Foyer foyer) {
        log.info("Ajout d'un nouveau foyer : {}", foyer.getNomFoyer());
        Foyer saved = foyerRepository.save(foyer);
        log.debug("Foyer ajouté avec l'id {}", saved.getIdFoyer());
        return saved;
    }

    @Override
    public List<Foyer> getAllFoyers() {
        log.info("Récupération de tous les foyers");
        List<Foyer> foyers = (List<Foyer>) foyerRepository.findAll();
        log.debug("Nombre de foyers récupérés : {}", foyers.size());
        return foyers;
    }

    @Override
    public Foyer getFoyerById(long idFoyer) {
        log.debug("Recherche du foyer avec l'id {}", idFoyer);
        return foyerRepository.findById(idFoyer).orElseThrow(() -> {
            log.error("Foyer introuvable avec l'id {}", idFoyer);
            return new NoSuchElementException("Foyer introuvable avec l'id " + idFoyer);
        });
    }

    @Override
    public void deleteFoyer(long idFoyer) {
        if (!foyerRepository.existsById(idFoyer)) {
            log.warn("Suppression demandée pour un foyer inexistant, id {}", idFoyer);
        }
        foyerRepository.deleteById(idFoyer);
        log.info("Suppression du foyer avec l'id {}", idFoyer);
    }

    @Override
    public Foyer updateFoyer(Foyer foyer) {
        log.info("Mise à jour du foyer avec l'id {}", foyer.getIdFoyer());
        return foyerRepository.save(foyer);
    }

    @Override
    public Foyer ajouterFoyerEtAffecterAUniversite(Foyer foyer, long idUniversite) {
        log.info("Ajout du foyer {} et affectation à l'université {}", foyer.getNomFoyer(), idUniversite);
        Foyer foyer1 = foyerRepository.save(foyer);
        Universite universite = universiteRepository.findById(idUniversite).orElseThrow(() -> {
            log.error("Université introuvable avec l'id {}", idUniversite);
            return new NoSuchElementException("Université introuvable avec l'id " + idUniversite);
        });
        universite.setFoyer(foyer1);
        universiteRepository.save(universite);
        log.debug("Foyer {} affecté à l'université {}", foyer1.getIdFoyer(), universite.getNomUniversite());
        return foyer1;
    }
}
