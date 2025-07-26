package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.*;
import com.gestioneDipendeti.GestioneDipendenti.Exception.AutoNonDisponibileException;
import com.gestioneDipendeti.GestioneDipendenti.Exception.RuoloContrattoNonValido;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AutoRepository;
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

    private AutoRepository autoRepository;

    private static final Logger logAutoService = Logger.getLogger(AutoService.class.getName());

    public AutoService(UtenteRepository utenteRepository, DipendenteRepository dipendenteRepository,
                       ContrattoRepository contrattoRepository, AutoRepository autoRepository){
        this.utenteRepository = utenteRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.contrattoRepository = contrattoRepository;
        this.autoRepository = autoRepository;
    }

    //Mi salvo il dipendente se ha diritto all'auto nel metodo verificaDipendetePerAuto
    Dipendente dipendenteAuto = null;

    public Dipendente verificaDipendetePerAuto(String username) throws IllegalArgumentException, ClassNotFoundException,Error,
            RuoloContrattoNonValido{
        Optional<Utente> utente = utenteRepository.findByUsername(username.trim());
        if (utente.isPresent()){
            Dipendente dipendente = dipendenteRepository.findByUtenteId(utente.get().getIdUtente());
            if (dipendente != null){
                Optional<Contratto> contratto = contrattoRepository.findBydipendente(dipendente);
                if (contratto.isPresent()){
                    if (!contratto.get().isScaduto()){
                        if (contratto.get().getLivelliContrattiCommercio().equals(LivelliContrattiCommercio.QUADRI)){
                            logAutoService.info("Il dipendente con id_dipendente " + dipendente.getIdDipendente() +
                                    " ha possibilità di avere la macchina");
                            dipendenteAuto = dipendente;
                            return dipendente;
                        }else {
                            logAutoService.warning(utente.get().getUsername() + " non hai diritto all'auto");
                            throw new RuoloContrattoNonValido(utente.get().getUsername() + " non hai diritto all'auto");
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

    public MacchinaAziendale richiestaIdAutoService(long idAuto)throws AutoNonDisponibileException,IllegalArgumentException{
        Optional<MacchinaAziendale> autoRichiesta = autoRepository.findById(idAuto);
        if (autoRichiesta.isPresent()){
            //verifico che l'auto sia disponibile e che non sia assegnata a nessun utente
            if (!autoRichiesta.get().isAutoDisponibile() && autoRichiesta.get().getDipendente() != null){
                logAutoService.warning("Auto non dispobile, idAuto : " + idAuto);
                throw new AutoNonDisponibileException("Auto non dispobile");
            }
            //setto il dipendente per l'auto e rendo l'auto non disponibile
            MacchinaAziendale auto = autoRichiesta.get();
            auto.setDipendente(dipendenteAuto);
            auto.setAutoDisponibile(false);
            return autoRepository.save(auto);
        }else {
            logAutoService.warning("idAuto: " + idAuto + " non valido");
            throw new IllegalArgumentException("idAuto: " + idAuto + " non valido");
        }
    }
}
