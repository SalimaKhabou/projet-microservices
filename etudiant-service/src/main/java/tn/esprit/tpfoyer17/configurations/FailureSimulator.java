package tn.esprit.tpfoyer17.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tn.esprit.tpfoyer17.entities.Etudiant;

@Component
public class FailureSimulator {

    @Value("${simulation.delay-ms:0}")
    private long delayMs;

    @Value("${simulation.corrupt:false}")
    private boolean corrupt;

    public Etudiant apply(Etudiant e) {
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        if (corrupt && e != null) {
            return Etudiant.builder()
                    .prenomEtudiant(e.getPrenomEtudiant())
                    .nomEtudiant(null)
                    .cinEtudiant(0)
                    .build();
        }
        return e;
    }
}