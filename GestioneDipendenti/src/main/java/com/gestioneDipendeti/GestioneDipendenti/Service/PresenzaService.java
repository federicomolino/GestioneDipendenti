package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;

@Service
public class PresenzaService {

    @Autowired
    private PresenzaRepository presenzaRepository;

    @Autowired
    private LoginService loginService;

    public Presenza addPreseza (Presenza presenza, Principal principal){
        //Setto id Utente
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = utente.getDipendente();
        presenza.setDipendente(dipendente);
        return presenzaRepository.save(presenza);
    }

    public void addPresenzaConFerie(Presenza presenza, Principal principal) throws IllegalArgumentException{
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = utente.getDipendente();
        LocalDate inizioFerie = presenza.getDataInizioFerie();
        LocalDate fineFerie = presenza.getDataFineFerie();

        if (inizioFerie.isAfter(fineFerie)){
            throw new IllegalArgumentException("Data inizio superiore a quella di fine");
        }

        for (LocalDate i = inizioFerie; !i.isAfter(fineFerie); i = i.plusDays(1)) {

            //Verifico se la data è già presente nel db per l'utente
            boolean presenzaData = presenzaRepository.existsByDataAndDipendente(i, dipendente);
            if (presenzaData){
                throw new IllegalArgumentException("Riga già presente nel db");
            }

            Presenza p = new Presenza();
            p.setData(i);
            p.setOraEntrata(null);
            p.setOraUscita(null);
            p.setModalita("FERIE");
            p.setStato(presenza.getStato());
            p.setDataInizioFerie(presenza.getDataInizioFerie());
            p.setDataFineFerie(presenza.getDataFineFerie());
            p.setDipendente(dipendente);
            presenzaRepository.save(p);
        }
    }

    public Presenza editPresenza(Presenza presenza){
        Presenza presenzaEsistente = presenzaRepository.findById(presenza.getIdPresenza()).get();
        presenzaEsistente.setOraEntrata(presenza.getOraEntrata());
        presenzaEsistente.setOraUscita(presenza.getOraUscita());
        presenzaEsistente.setModalita(presenza.getModalita());
        presenzaEsistente.setStato(presenza.getStato());
        return presenzaRepository.save(presenzaEsistente);
    }
}
