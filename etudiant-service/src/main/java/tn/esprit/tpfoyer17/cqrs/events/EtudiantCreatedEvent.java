package tn.esprit.tpfoyer17.cqrs.events;

public record EtudiantCreatedEvent(String id, String nom, String prenom, long cin) {
}