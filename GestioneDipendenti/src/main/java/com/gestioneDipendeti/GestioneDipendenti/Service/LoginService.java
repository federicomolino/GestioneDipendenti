package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class LoginService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    public Utente recuperoUtente(Principal principal){
        String username = principal.getName();
        Utente user = utenteRepository.findByUsername(username).get();
        return user;
    }

    public Dipendente recuperoDipendente(Principal principal){
        Utente user = recuperoUtente(principal);
        Long idUtente = user.getIdUtente();
        Dipendente dipendente = dipendenteRepository.findById(idUtente).get();
        return dipendente;
    }
}
