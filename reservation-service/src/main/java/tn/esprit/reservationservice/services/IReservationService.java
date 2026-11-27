package tn.esprit.reservationservice.services;

import tn.esprit.reservationservice.entities.Reservation;

import java.util.List;

public interface IReservationService {
    List<Reservation> getAllReservations();
    Reservation getReservationById(String idReservation);
    void deleteReservation(String idReservation);
    Reservation updateReservation(Reservation reservation);
    Reservation ajouterReservation(long idBloc, long cinEtudiant);
    Reservation annulerReservation(long cinEtudiant);
}