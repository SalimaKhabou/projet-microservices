package tn.esprit.tpfoyer17.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer17.entities.Universite;
import tn.esprit.tpfoyer17.repositories.FoyerRepository;
import tn.esprit.tpfoyer17.repositories.UniversiteRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UniversiteService implements IUniversiteService{
    UniversiteRepository universiteRepository;
    FoyerRepository foyerRepository;

    @Override
    public Universite addUniversite(Universite universite) {
        log.info("Ajout d'une nouvelle université : {}", universite.getNomUniversite());
        Universite saved = universiteRepository.save(universite);
        log.debug("Université ajoutée avec l'id {}", saved.getIdUniversite());
        return saved;
    }

    @Override
    public List<Universite> getAllUniversites() {
        log.info("Récupération de toutes les universités");
        List<Universite> universites = (List<Universite>) universiteRepository.findAll();
        log.debug("Nombre d'universités récupérées : {}", universites.size());
        return universites;
    }

    @Override
    public Universite getUniversiteById(long idUniversite) {
        log.debug("Recherche de l'université avec l'id {}", idUniversite);
        return trouverUniversite(idUniversite);
    }

    @Override
    public void deleteUniversite(long idUniversite) {
        if (!universiteRepository.existsById(idUniversite)) {
            log.warn("Suppression demandée pour une université inexistante, id {}", idUniversite);
        }
        universiteRepository.deleteById(idUniversite);
        log.info("Suppression de l'université avec l'id {}", idUniversite);
    }

    @Override
    public Universite updateUniversite(Universite universite) {
        log.info("Mise à jour de l'université avec l'id {}", universite.getIdUniversite());
        return universiteRepository.save(universite);
    }

    @Override
    public Universite affecterFoyerAUniversite(long idFoyer, String nomUniversite) {
        log.info("Affectation du foyer {} à l'université {}", idFoyer, nomUniversite);
        Universite universite = universiteRepository.findByNomUniversite(nomUniversite);
        if (universite == null) {
            log.error("Université introuvable avec le nom {}", nomUniversite);
            throw new NoSuchElementException("Université introuvable avec le nom " + nomUniversite);
        }
        universite.setFoyer(foyerRepository.findById(idFoyer).orElseThrow(() -> {
            log.error("Foyer introuvable avec l'id {}", idFoyer);
            return new NoSuchElementException("Foyer introuvable avec l'id " + idFoyer);
        }));
        return universiteRepository.save(universite);
    }

    @Override
    public Universite desaffecterFoyerAUniversite(long idUniversite) {
        log.info("Désaffectation du foyer de l'université {}", idUniversite);
        Universite universite = trouverUniversite(idUniversite);
        universite.setFoyer(null);
        return universiteRepository.save(universite);
    }

    private Universite trouverUniversite(long idUniversite) {
        return universiteRepository.findById(idUniversite).orElseThrow(() -> {
            log.error("Université introuvable avec l'id {}", idUniversite);
            return new NoSuchElementException("Université introuvable avec l'id " + idUniversite);
        });
    }
}
