package tn.esprit.tpfoyer17.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer17.entities.Etudiant;
import tn.esprit.tpfoyer17.services.IEtudiantService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("api/etudiants")
public class EtudiantController {
    IEtudiantService etudiantService;

    @PostMapping("add")
    public Etudiant addEtudiant(@Valid @RequestBody Etudiant etudiant){
        return etudiantService.addEtudiant(etudiant);
    }

    @GetMapping("getAll")
    public List<Etudiant> gettingAllEtudiant(){
        return etudiantService.getAllEtudiants();
    }

    @GetMapping("get")
    public Etudiant gettingEtudiant(@RequestParam("idEtudiant") long idEtudiant){
        return etudiantService.getEtudiantById(idEtudiant);
    }

    @DeleteMapping("delete/{idEtudiant}")
    public void deletingEtudiant(@PathVariable("idEtudiant") long idEtudiant){
        etudiantService.deleteEtudiant(idEtudiant);
    }

    @PutMapping("update")
    public Etudiant updatingEtudiant(@RequestBody Etudiant etudiant){
        return etudiantService.updateEtudiant(etudiant);
    }
}
