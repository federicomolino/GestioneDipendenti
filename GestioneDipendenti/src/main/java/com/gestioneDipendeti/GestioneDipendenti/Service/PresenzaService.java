package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PresenzaService {

    @Autowired
    private PresenzaRepository presenzaRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ContrattoRepository contrattoRepository;


    public Presenza addPreseza (Presenza presenza, Principal principal){
        //Setto id Utente
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = utente.getDipendente();
        presenza.setDipendente(dipendente);
        return presenzaRepository.save(presenza);
    }

    public void addPresenzaConFerie(Presenza presenza, Principal principal) throws IllegalArgumentException, ArithmeticException{
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
            //Verifico se esiste un contratto per il dipendente
            Optional<Contratto> contrattoDipendente = contrattoRepository.findBydipendente(dipendente);
            if (contrattoDipendente.isPresent()){
                Presenza p = new Presenza();
                p.setData(i);
                p.setOraEntrata(null);
                p.setOraUscita(null);
                p.setModalita("FERIE");
                p.setStato(presenza.getStato());
                p.setDataInizioFerie(presenza.getDataInizioFerie());
                p.setDataFineFerie(presenza.getDataFineFerie());
                p.setDipendente(dipendente);
                contrattoDipendente.get().setOreFerieUtilizzate(contrattoDipendente.get().getOreFerieUtilizzate()+8);
                if (contrattoDipendente.get().getOreFerieUtilizzate() > contrattoDipendente.get().getOreFerieTotali()){
                    throw new ArithmeticException("Limite massimo di ore ferie superato");
                }
                contrattoRepository.save(contrattoDipendente.get());
                presenzaRepository.save(p);
            }else {
                throw new IllegalArgumentException("L'utente non ha nessun contratto!!");
            }
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
