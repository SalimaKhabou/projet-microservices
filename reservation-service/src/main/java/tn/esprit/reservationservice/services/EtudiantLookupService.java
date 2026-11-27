package tn.esprit.reservationservice.services;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.reservationservice.clients.EtudiantClient;
import tn.esprit.reservationservice.dto.EtudiantDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class EtudiantLookupService {

    private final EtudiantClient etudiantClient;

    // Retry est l'aspect le plus externe : le fallback n'est appelé qu'après les 3 tentatives
    /*@Retry(name = "etudiantService", fallbackMethod = "fallback")
    @CircuitBreaker(name = "etudiantService")
    @RateLimiter(name = "etudiantService")
    public EtudiantDto getEtudiant(long cin) {
        log.info("Appel de etudiant-service pour le CIN {}", cin);
        return etudiantClient.getByCin(cin);
    }*/
    @Retry(name = "etudiantService", fallbackMethod = "fallback")
    @CircuitBreaker(name = "etudiantService")
    @RateLimiter(name = "etudiantService")
    public EtudiantDto getEtudiant(long cin) {
        log.info("Appel de etudiant-service pour le CIN {}", cin);
        EtudiantDto e = etudiantClient.getByCin(cin);
        if (e == null || e.cinEtudiant() != cin || e.nomEtudiant() == null) {
            throw new IllegalStateException("Réponse invalide de etudiant-service pour le CIN " + cin);
        }
        return e;
    }

    // Étudiant inexistant : réponse métier normale
    public EtudiantDto fallback(long cin, FeignException.NotFound e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Etudiant introuvable : CIN " + cin);
    }

    // Rate Limiter : trop de requêtes
    public EtudiantDto fallback(long cin, RequestNotPermitted e) {
        log.warn("Limite de débit atteinte pour etudiant-service");
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Trop de requêtes, réessayez dans un instant");
    }

    // Circuit ouvert : on n'appelle même plus etudiant-service
    public EtudiantDto fallback(long cin, CallNotPermittedException e) {
        log.warn("Circuit ouvert : etudiant-service est considéré indisponible");
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service étudiant temporairement indisponible (circuit ouvert)");
    }

    // Toute autre panne (service arrêté, timeout, erreur réseau...)
    public EtudiantDto fallback(long cin, Throwable t) {
        log.warn("Échec de l'appel à etudiant-service : {}", t.toString());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service étudiant indisponible, réservation impossible pour le moment");
    }

    public EtudiantDto fallback(long cin, IllegalStateException e) {
        log.warn("Données incorrectes reçues de etudiant-service : {}", e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Réponse invalide du service étudiant");
    }
}