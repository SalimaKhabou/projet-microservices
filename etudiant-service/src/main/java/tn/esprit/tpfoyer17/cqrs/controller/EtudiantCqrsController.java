package tn.esprit.tpfoyer17.cqrs.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.tpfoyer17.cqrs.commands.CreateEtudiantCommand;
import tn.esprit.tpfoyer17.cqrs.commands.UpdateEtudiantCommand;
import tn.esprit.tpfoyer17.cqrs.query.EtudiantView;
import tn.esprit.tpfoyer17.cqrs.query.FindAllEtudiantsQuery;
import tn.esprit.tpfoyer17.cqrs.query.FindEtudiantByIdQuery;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/cqrs/etudiants")
@RequiredArgsConstructor
public class EtudiantCqrsController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    // ----- COMMANDES (écriture) -----
    public record EtudiantRequest(String nom, String prenom, long cin) {
    }

    @PostMapping("commands/create")
    public CompletableFuture<String> create(@RequestBody EtudiantRequest r) {
        return commandGateway.send(
                new CreateEtudiantCommand(UUID.randomUUID().toString(), r.nom(), r.prenom(), r.cin()));
    }

    @PutMapping("commands/update/{id}")
    public CompletableFuture<Void> update(@PathVariable String id, @RequestBody EtudiantRequest r) {
        return commandGateway.send(new UpdateEtudiantCommand(id, r.nom(), r.prenom()));
    }

    // ----- REQUÊTES (lecture) -----
    @GetMapping("queries/all")
    public CompletableFuture<List<EtudiantView>> all() {
        return queryGateway.query(new FindAllEtudiantsQuery(), ResponseTypes.multipleInstancesOf(EtudiantView.class));
    }

    @GetMapping("queries/{id}")
    public CompletableFuture<EtudiantView> byId(@PathVariable String id) {
        return queryGateway.query(new FindEtudiantByIdQuery(id), ResponseTypes.instanceOf(EtudiantView.class))
                .thenApply(v -> {
                    if (v == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Etudiant introuvable");
                    }
                    return v;
                });
    }
}