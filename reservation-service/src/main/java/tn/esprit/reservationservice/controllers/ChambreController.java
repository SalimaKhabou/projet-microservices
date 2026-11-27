package tn.esprit.reservationservice.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tn.esprit.reservationservice.entities.Chambre;
import tn.esprit.reservationservice.entities.enumerations.TypeChambre;
import tn.esprit.reservationservice.services.IChambreService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("api/chambres")
public class ChambreController {
    IChambreService chambreService;

    @PostMapping("add")
    public Chambre addingChambre(@RequestBody Chambre chambre) {
        return chambreService.addChambre(chambre);
    }

    @GetMapping("getAll")
    public List<Chambre> gettingAllChambre() {
        return chambreService.getAllChambres();
    }

    @GetMapping("get")
    public Chambre gettingChambre(@RequestParam("idChambre") long idChambre) {
        return chambreService.getChambreById(idChambre);
    }

    @DeleteMapping("delete/{idChambre}")
    public void deletingChambre(@PathVariable("idChambre") long idChambre) {
        chambreService.deleteChambre(idChambre);
    }

    @PutMapping("update")
    public Chambre updatingChambre(@RequestBody Chambre chambre) {
        return chambreService.updateChambre(chambre);
    }

    @GetMapping("get-par-bloc-type-keyword")
    public List<Chambre> getChambresParBlocEtTypeKeyWord(@RequestParam("idBloc") long idBloc, @RequestParam("typeC") TypeChambre typeC) {
        return chambreService.getChambresParBlocEtTypeKeyWord(idBloc, typeC);
    }

    @GetMapping("get-par-bloc-type-jpql")
    public List<Chambre> getChambresParBlocEtTypeJPQL(@RequestParam("idBloc") long idBloc, @RequestParam("typeC") TypeChambre typeC) {
        return chambreService.getChambresParBlocEtTypeJPQL(idBloc, typeC);
    }
}