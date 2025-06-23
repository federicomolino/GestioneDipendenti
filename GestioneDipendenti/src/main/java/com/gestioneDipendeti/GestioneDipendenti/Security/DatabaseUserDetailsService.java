package com.gestioneDipendeti.GestioneDipendenti.Security;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class DatabaseUserDetailsService implements UserDetailsService {
    @Autowired
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Utente> optUtente = utenteRepository.findByUsername(username);

        if (optUtente.isPresent()){
            return new DatabaseUserDetails(optUtente.get());
        }else {
            throw new UsernameNotFoundException("Utente non valido");
        }
    }
}
