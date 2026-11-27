package tn.esprit.reservationservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tn.esprit.reservationservice.entities.Bloc;
import tn.esprit.reservationservice.services.IBlocService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("api/blocs")
@Tag(name = "Gestion des Blocs", description = "les apis pour gérer tous les blocs")
public class BlocController {
    IBlocService blocService;

    @PostMapping("add")
    public Bloc addingBloc(@RequestBody Bloc bloc) {
        return blocService.addBloc(bloc);
    }

    @GetMapping("getAll")
    public List<Bloc> gettingAllBloc() {
        return blocService.getAllBlocs();
    }

    @GetMapping("get")
    public Bloc gettingBloc(@RequestParam("idBloc") long idBloc) {
        return blocService.getBlocById(idBloc);
    }

    @DeleteMapping("delete/{idBloc}")
    public void deletingBloc(@PathVariable("idBloc") long idBloc) {
        blocService.deleteBloc(idBloc);
    }

    @PutMapping("update")
    public Bloc updatingBloc(@RequestBody Bloc bloc) {
        return blocService.updateBloc(bloc);
    }

    @PutMapping("affecter-chambre-bloc")
    public Bloc affecterChambresABloc(@RequestBody List<Long> numChambre, @RequestParam("idBloc") long idBloc) {
        return blocService.affecterChambresABloc(numChambre, idBloc);
    }
}