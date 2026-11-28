package tn.esprit.tpfoyer17.cqrs.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import tn.esprit.tpfoyer17.cqrs.events.EtudiantCreatedEvent;
import tn.esprit.tpfoyer17.cqrs.events.EtudiantUpdatedEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtudiantProjection {

    private final EtudiantViewRepository repository;

    @EventHandler
    public void on(EtudiantCreatedEvent e) {
        log.info("Projection : création de {}", e.id());
        repository.save(new EtudiantView(e.id(), e.nom(), e.prenom(), e.cin()));
    }

    @EventHandler
    public void on(EtudiantUpdatedEvent e) {
        log.info("Projection : mise à jour de {}", e.id());
        repository.findById(e.id()).ifPresent(v -> {
            v.setNom(e.nom());
            v.setPrenom(e.prenom());
            repository.save(v);
        });
    }

    @QueryHandler
    public List<EtudiantView> handle(FindAllEtudiantsQuery q) {
        return repository.findAll();
    }

    @QueryHandler
    public EtudiantView handle(FindEtudiantByIdQuery q) {
        return repository.findById(q.id()).orElse(null);
    }
}