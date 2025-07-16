package com.gestioneDipendeti.GestioneDipendenti.RestAPI;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.StatoPresenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.PresenzaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/v1/presenza")
public class RestApiPresenza {

    private PresenzaRepository presenzaRepository;

    private PresenzaService presenzaService;

    private DipendenteRepository dipendenteRepository;

    private UtenteRepository utenteRepository;

    private final Logger logRestApiPresenza = Logger.getLogger(RestApiPresenza.class.getName());

    public RestApiPresenza(PresenzaRepository presenzaRepository, PresenzaService presenzaService, DipendenteRepository
                           dipendenteRepository, UtenteRepository utenteRepository){
        this.presenzaRepository = presenzaRepository;
        this.presenzaService = presenzaService;
        this.dipendenteRepository = dipendenteRepository;
        this.utenteRepository = utenteRepository;
    }


    @PostMapping("/{id}")
    public ResponseEntity<Map<String, Object>> addPresenzaApi(@PathVariable("id")Long id, @RequestBody Presenza presenza){
        Optional<Dipendente> dipendente = dipendenteRepository.findById(id);
        if (dipendente.isEmpty()){
            logRestApiPresenza.warning("Nessun dipendente trovato");
            return ResponseEntity.internalServerError().body(Map.of("Nessun dipendente trovato", presenza));
        }

        Optional<Utente> utente = utenteRepository.findById(dipendente.get().getUtente().getIdUtente());
        if (utente.isEmpty()){
            logRestApiPresenza.warning("Nessun utente trovato");
            return ResponseEntity.internalServerError().body(Map.of("Utente non trovata", presenza));
        }

        //Usiamo lamba(classe anonima) evitiamo di crearci una classe apposta per trasformare stringa --> principal
        Principal principal = () -> utente.get().getUsername();
        if (presenza.getStato().equals(StatoPresenza.PRESENTE) || presenza.getStato().equals(StatoPresenza.PERMESSO)){
            try {
                presenzaService.addPreseza(presenza,principal);
            }catch (DataFormatException exdata){
                logRestApiPresenza.warning("Date inserite non valide");
                return ResponseEntity.badRequest().body(Map.of("Date inserite non valide", presenza));
            }catch (IllegalArgumentException ex){
                logRestApiPresenza.warning("Data già presente a sistema");
                return ResponseEntity.badRequest().body(Map.of("Data già presente a sistema", presenza));
            }
        } else if (presenza.getStato().equals(StatoPresenza.FERIE)) {
            try {
                presenzaService.addPresenzaConFerie(presenza,principal);
            }catch (IllegalArgumentException ex){
                logRestApiPresenza.warning("Data già presente a sistema");
                return ResponseEntity.badRequest().body(Map.of("Data già presente a sistema", presenza));
            }catch (ArithmeticException ex){
                logRestApiPresenza.warning("Limite massimo di ore ferie superato");
                return ResponseEntity.badRequest().body(Map.of("Limite massimo di ore ferie superato", presenza));
            }
        }
        logRestApiPresenza.info("Presenza segnata");
        return ResponseEntity.ok().body(Map.of("messaggio: ", presenza));
    }
}
