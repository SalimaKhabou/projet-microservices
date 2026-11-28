package tn.esprit.tpfoyer17.cqrs.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateEtudiantCommand(
        @TargetAggregateIdentifier String id,
        String nom, String prenom) {
}