package tn.esprit.reservationservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.esprit.reservationservice.dto.EtudiantDto;

@FeignClient(name = "etudiant-service")
//tester Erreurs réseau : URL mal configurée
//@FeignClient(name = "etudiant-service", url = "http://localhost:9999")

public interface EtudiantClient {

    @GetMapping("/api/etudiants/getByCin")
    EtudiantDto getByCin(@RequestParam("cin") long cin);
}