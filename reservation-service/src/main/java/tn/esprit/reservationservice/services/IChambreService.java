package tn.esprit.reservationservice.services;

import tn.esprit.reservationservice.entities.Chambre;
import tn.esprit.reservationservice.entities.enumerations.TypeChambre;

import java.util.List;

public interface IChambreService {
    Chambre addChambre(Chambre chambre);
    List<Chambre> getAllChambres();
    Chambre getChambreById(long idChambre);
    void deleteChambre(long idChambre);
    Chambre updateChambre(Chambre chambre);
    List<Chambre> getChambresParBlocEtTypeKeyWord(long idBloc, TypeChambre typeC);
    List<Chambre> getChambresParBlocEtTypeJPQL(long idBloc, TypeChambre typeC);
}