package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.*;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

@Service
public class PresenzaService {

    @Autowired
    private PresenzaRepository presenzaRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ContrattoRepository contrattoRepository;

    private static final Logger log = Logger.getLogger(PresenzaService.class.getName());


    public Presenza addPreseza (Presenza presenza, Principal principal) throws DataFormatException{
        //Setto id Utente
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = utente.getDipendente();

        if (presenza.getStato().equals(StatoPresenza.PERMESSO)){
            try {
                addPresenzaConPermesso(presenza, principal);
            }catch (DataFormatException ex){
                throw ex;
            }
        }
        presenza.setDipendente(dipendente);
        return presenzaRepository.save(presenza);
    }

    public void addPresenzaConFerie(Presenza presenza, Principal principal) throws IllegalArgumentException, ArithmeticException{
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = utente.getDipendente();
        LocalDate inizioFerie = presenza.getDataInizioFerie();
        LocalDate fineFerie = presenza.getDataFineFerie();

        if (inizioFerie.isAfter(fineFerie)){
            log.warning("Data inizio superiore a quella di fine");
            throw new IllegalArgumentException("Data inizio superiore a quella di fine");
        }

        for (LocalDate i = inizioFerie; !i.isAfter(fineFerie); i = i.plusDays(1)) {

            //Verifico se la data è già presente nel db per l'utente
            boolean presenzaData = presenzaRepository.existsByDataAndDipendente(i, dipendente);
            if (presenzaData){
                log.warning("Riga già presente nel db");
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
                    log.warning("Limite massimo di ore ferie superato");
                    throw new ArithmeticException("Limite massimo di ore ferie superato");
                }
                contrattoRepository.save(contrattoDipendente.get());
                presenzaRepository.save(p);
            }else {
                log.warning("L'utente non ha nessun contratto!!");
                throw new IllegalArgumentException("L'utente non ha nessun contratto!!");
            }
        }
    }

    public void addPresenzaConPermesso(Presenza presenza, Principal principal) throws DataFormatException {
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = utente.getDipendente();
        LocalTime oraUscita = presenza.getOraUscita();
        LocalTime oraEntrata = presenza.getOraEntrata();

        if (oraUscita.isAfter(oraEntrata)){
            log.warning("Ora uscita più grande dell'oraEntrata");
            throw new DataFormatException("Ora uscita più grande dell'oraEntrata");
        }

        //Verifico se esiste un contratto per il dipendente
        Optional<Contratto> contrattoDipendente = contrattoRepository.findBydipendente(dipendente);
        if(contrattoDipendente.isPresent()){
            //Restituisce il totale dei minuti
            Duration durataTotalePermesso = Duration.between(oraUscita,oraEntrata);
            // Converto la durata divindendo per 60 per prendermi le ore e metterlo in float
            float oreTotaliPermessoConvertitoInFloat = durataTotalePermesso.toMinutes() / 60.0f;
            contrattoDipendente.get().setOreFerieUtilizzate(contrattoDipendente.get().getOreFerieUtilizzate() + oreTotaliPermessoConvertitoInFloat);
            Contratto contrattoDipendenteEsistente = contrattoDipendente.get();
            contrattoRepository.save(contrattoDipendenteEsistente);
        }
    }

    public Presenza editPresenza(Presenza presenza, Principal principal) throws DataFormatException {
        Presenza presenzaEsistente = presenzaRepository.findById(presenza.getIdPresenza()).get();
        if (presenza.getStato().equals(StatoPresenza.PERMESSO)){
            Utente utente = loginService.recuperoUtente(principal);
            Dipendente dipendente = utente.getDipendente();

            LocalTime oraUscita = presenzaEsistente.getOraUscita();
            LocalTime oraEntrata = presenzaEsistente.getOraEntrata();
            LocalTime oraUscitaInput = presenza.getOraUscita();
            LocalTime oraEntrataInput = presenza.getOraEntrata();

            if (oraUscitaInput.isAfter(oraEntrataInput)){
                log.warning("Ora uscita più grande dell'oraEntrata");
                throw new DataFormatException("Ora uscita più grande dell'oraEntrata");
            }

            //Durante salvata
            Duration differenzaPresenteSalvata = Duration.between(oraUscita,oraEntrata);
            float oreTotaliPermessoConvertitoInFloat = differenzaPresenteSalvata.toMinutes() / 60.0f;
            //Durante input
            Duration differenzaPresenteInput = Duration.between(oraUscitaInput,oraEntrataInput);
            float oreTotaliPermessoInputConvertitoInFloat = differenzaPresenteInput.toMinutes() / 60.0f;
            //Mi recupero il contratto
            Contratto contratto = contrattoRepository.findBydipendente(dipendente).get();
            if (oreTotaliPermessoConvertitoInFloat > oreTotaliPermessoInputConvertitoInFloat){
                float diferenzaDaAumentare = oreTotaliPermessoConvertitoInFloat - oreTotaliPermessoInputConvertitoInFloat;
                contratto.setOreFerieUtilizzate(contratto.getOreFerieUtilizzate() - diferenzaDaAumentare);
                contrattoRepository.save(contratto);
            }else {
                float diferenzaDaAumentare = oreTotaliPermessoConvertitoInFloat - oreTotaliPermessoInputConvertitoInFloat;
                contratto.setOreFerieUtilizzate(contratto.getOreFerieUtilizzate() + diferenzaDaAumentare);
                contrattoRepository.save(contratto);
            }

        }
        presenzaEsistente.setOraEntrata(presenza.getOraEntrata());
        presenzaEsistente.setOraUscita(presenza.getOraUscita());
        presenzaEsistente.setModalita(presenza.getModalita());
        presenzaEsistente.setStato(presenza.getStato());
        return presenzaRepository.save(presenzaEsistente);
    }

    public boolean chiudiGiornata(Long idPresenza) throws ArithmeticException{
        Presenza presenza = presenzaRepository.findById(idPresenza).get();
        if (presenza.isChiudiGiornata()){
            presenza.setChiudiGiornata(false);
            presenzaRepository.save(presenza);
            return false;
        }

        if (presenza.getStato().equals(StatoPresenza.PRESENTE) ||
                presenza.getStato().equals(StatoPresenza.PERMESSO)){

            LocalTime oraEntrata = presenza.getOraEntrata();
            LocalTime oraUscita = presenza.getOraUscita();

            Duration durataGionarta = Duration.between(oraEntrata,oraUscita);
            float durataGionartaInMinuti = durataGionarta.toHours();
            if (durataGionartaInMinuti < 8){
                log.warning("Giornata non chiudibile, meno di 8 ore fatte!!");
                throw new ArithmeticException("Giornata non chiudibile");
            }else {
                presenza.setChiudiGiornata(true);
                presenzaRepository.save(presenza);
                return true;
            }
        }else {
            presenza.setChiudiGiornata(true);
            presenzaRepository.save(presenza);
            return true;
        }
    }
}
