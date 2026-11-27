package tn.esprit.reservationservice.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.reservationservice.entities.Chambre;
import tn.esprit.reservationservice.entities.enumerations.TypeChambre;
import tn.esprit.reservationservice.repositories.ChambreRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChambreService implements IChambreService {
    ChambreRepository chambreRepository;

    @Override
    public Chambre addChambre(Chambre chambre) {
        return chambreRepository.save(chambre);
    }

    @Override
    public List<Chambre> getAllChambres() {
        return (List<Chambre>) chambreRepository.findAll();
    }

    @Override
    public Chambre getChambreById(long idChambre) {
        return chambreRepository.findById(idChambre).orElseThrow();
    }

    @Override
    public void deleteChambre(long idChambre) {
        chambreRepository.deleteById(idChambre);
    }

    @Override
    public Chambre updateChambre(Chambre chambre) {
        return chambreRepository.save(chambre);
    }

    @Override
    public List<Chambre> getChambresParBlocEtTypeKeyWord(long idBloc, TypeChambre typeC) {
        return chambreRepository.findByBlocIdBlocAndTypeChambre(idBloc, typeC);
    }

    @Override
    public List<Chambre> getChambresParBlocEtTypeJPQL(long idBloc, TypeChambre typeC) {
        return chambreRepository.findByBlocIdBlocAndTypeChambreJPQL(idBloc, typeC);
    }
}