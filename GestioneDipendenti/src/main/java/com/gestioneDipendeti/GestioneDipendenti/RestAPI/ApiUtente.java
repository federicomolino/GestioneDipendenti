package com.gestioneDipendeti.GestioneDipendenti.RestAPI;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/utente")
public class ApiUtente {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    @GetMapping
    public ResponseEntity<?> showUtenti(){
        List<Utente> utenti = utenteRepository.findAll();
        if (utenti.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(utenti);
    }

    @GetMapping("/dipendenti")
    public List<Dipendente> showDipendente(){
        List<Dipendente> dipendenti = dipendenteRepository.findAll();
        if (dipendenti.isEmpty()){
            ResponseEntity.notFound().build();
            return dipendenti;
        }
        ResponseEntity.ok(dipendenti);
        return dipendenti;
    }
}
