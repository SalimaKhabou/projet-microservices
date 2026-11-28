package tn.esprit.tpfoyer17.cqrs.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import tn.esprit.tpfoyer17.cqrs.commands.CreateEtudiantCommand;
import tn.esprit.tpfoyer17.cqrs.commands.UpdateEtudiantCommand;
import tn.esprit.tpfoyer17.cqrs.events.EtudiantCreatedEvent;
import tn.esprit.tpfoyer17.cqrs.events.EtudiantUpdatedEvent;

@Aggregate
public class EtudiantAggregate {

    @AggregateIdentifier
    private String id;
    private String nom;
    private String prenom;
    private long cin;

    protected EtudiantAggregate() {
        // requis par Axon
    }

    @CommandHandler
    public EtudiantAggregate(CreateEtudiantCommand cmd) {
        if (cmd.nom() == null || cmd.nom().isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (cmd.cin() <= 0) {
            throw new IllegalArgumentException("Le CIN doit être positif");
        }
        AggregateLifecycle.apply(new EtudiantCreatedEvent(cmd.id(), cmd.nom(), cmd.prenom(), cmd.cin()));
    }

    @CommandHandler
    public void handle(UpdateEtudiantCommand cmd) {
        if (cmd.nom() == null || cmd.nom().isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        AggregateLifecycle.apply(new EtudiantUpdatedEvent(cmd.id(), cmd.nom(), cmd.prenom()));
    }

    @EventSourcingHandler
    public void on(EtudiantCreatedEvent e) {
        this.id = e.id();
        this.nom = e.nom();
        this.prenom = e.prenom();
        this.cin = e.cin();
    }

    @EventSourcingHandler
    public void on(EtudiantUpdatedEvent e) {
        this.nom = e.nom();
        this.prenom = e.prenom();
    }
}