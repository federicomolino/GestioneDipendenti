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
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/v1/presenza")
public class RestApiPresenza {

    private PresenzaRepository presenzaRepository;

    private PresenzaService presenzaService;

    private DipendenteRepository dipendenteRepository;

    private UtenteRepository utenteRepository;

    public RestApiPresenza(PresenzaRepository presenzaRepository, PresenzaService presenzaService, DipendenteRepository
                           dipendenteRepository, UtenteRepository utenteRepository){
        this.presenzaRepository = presenzaRepository;
        this.presenzaService = presenzaService;
        this.dipendenteRepository = dipendenteRepository;
        this.utenteRepository = utenteRepository;
    }

    @GetMapping()
    public ResponseEntity<?> addPresenza(){
        List<Presenza> p = presenzaRepository.findAll();
        return ResponseEntity.ok(p);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> addPresenzaApi(@PathVariable("id")Long id, @RequestBody Presenza presenza){
        Dipendente dipendente = dipendenteRepository.findById(id).get();
        Optional<Utente> utente = utenteRepository.findById(dipendente.getUtente().getIdUtente());
        if (utente.isEmpty()){
            ResponseEntity.internalServerError();
        }

        //Usiamo lamba(classe anonima) evitiamo di crearci una classe apposta per trasformare stringa --> principal
        Principal principal = () -> utente.get().getUsername();
        if (presenza.getStato().equals(StatoPresenza.PRESENTE) || presenza.getStato().equals(StatoPresenza.PERMESSO)){
            try {
                presenzaService.addPreseza(presenza,principal);
            }catch (DataFormatException exdata){
                return ResponseEntity.badRequest().body("Date inserite non valide");
            }catch (IllegalArgumentException ex){
                return ResponseEntity.badRequest().body("Data già presente a sistema");
            }
        } else if (presenza.getStato().equals(StatoPresenza.FERIE)) {
            try {
                presenzaService.addPresenzaConFerie(presenza,principal);
            }catch (IllegalArgumentException ex){
                return ResponseEntity.badRequest().body("Data già presente a sistema");
            }catch (ArithmeticException ex){
                return ResponseEntity.badRequest().body("Limite massimo di ore ferie superato");
            }
        }
        return ResponseEntity.ok().body(presenza + " Presenza segnata");
    }
}
