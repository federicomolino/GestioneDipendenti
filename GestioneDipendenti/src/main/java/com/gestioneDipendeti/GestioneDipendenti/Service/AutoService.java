package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.LivelliContrattiCommercio;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AutoService {

    private UtenteRepository utenteRepository;

    private DipendenteRepository dipendenteRepository;

    private ContrattoRepository contrattoRepository;

    private static final Logger logAutoService = Logger.getLogger(AutoService.class.getName());

    public AutoService(UtenteRepository utenteRepository, DipendenteRepository dipendenteRepository,
                       ContrattoRepository contrattoRepository){
        this.utenteRepository = utenteRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.contrattoRepository = contrattoRepository;
    }

    public Dipendente verificaDipendetePerAuto(String username) throws IllegalArgumentException, ClassNotFoundException,Error{
        Optional<Utente> utente = utenteRepository.findByUsername(username.trim());
        if (utente.isPresent()){
            Dipendente dipendente = dipendenteRepository.findByUtenteId(utente.get().getIdUtente());
            if (dipendente != null){
                Optional<Contratto> contratto = contrattoRepository.findBydipendente(dipendente);
                if (contratto.isPresent()){
                    if (!contratto.get().isScaduto()){
                        if (contratto.get().getLivelliContrattiCommercio().equals(LivelliContrattiCommercio.QUADRI)){
                            logAutoService.info("Il dipendente con id_dipendente" + dipendente.getIdDipendente() +
                                    " ha possibilità di avere la macchina");
                            return dipendente;
                        }
                    }else {
                        logAutoService.warning("Contratto scaduto per id_dipendente " + dipendente.getIdDipendente());
                        throw new Error("contratto scaduto");
                    }
                }else {
                    logAutoService.warning("Nessun Contratto per id_dipendente " + dipendente.getIdDipendente());
                    throw new ClassNotFoundException("Nessun contratto per il dipendete");
                }
            }
        }else {
            logAutoService.warning("Utente inserito non valido: " + username);
            throw new IllegalArgumentException("Utente inserito non valido");
        }
        return null;
    }
}
