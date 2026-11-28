package tn.esprit.tpfoyer17.cqrs.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreateEtudiantCommand(
        @TargetAggregateIdentifier String id,
        String nom, String prenom, long cin) {
}