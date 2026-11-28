package tn.esprit.tpfoyer17.cqrs.events;

public record EtudiantUpdatedEvent(String id, String nom, String prenom) {
}