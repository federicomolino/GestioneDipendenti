package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.*;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
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


    public Presenza addPreseza (Presenza presenza, Principal principal) throws IllegalArgumentException, DataFormatException, DateTimeException{
        //Setto id Utente
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = utente.getDipendente();
        LocalTime oraEntrata = presenza.getOraEntrata();
        LocalTime oraUscita = presenza.getOraUscita();

        //Controllo se la data è di sabato o dominica
        if (presenza.getData().getDayOfWeek()  == DayOfWeek.SATURDAY || presenza.getData().getDayOfWeek() == DayOfWeek.SUNDAY){
            log.warning("Non è possibile aggiungere di sabato o domenica la presenza");
            throw new DateTimeException("Non è possibile aggiungere di sabato o domenica la presenza");
        }

        //Presenza già presente a DB
        List<Presenza> presenzaData = presenzaRepository.findByDataAndDipendente(presenza.getData(), dipendente);
        if (!presenzaData.isEmpty()){
            int oreTotaliSvolte = 0;
            List<Integer> giornataStrardinario = new ArrayList<>();
            for (Presenza p : presenzaData){
                int OraEntrataPresenza = p.getOraEntrata().getHour();
                int oraUscitaPresenza = p.getOraUscita().getHour();
                int oreFattePerGiornata = oraUscitaPresenza - OraEntrataPresenza;
                oreTotaliSvolte += oreFattePerGiornata;
                if (p.isGiornataStraordinario()){
                    giornataStrardinario.add(1);
                }else {
                    giornataStrardinario.add(0);
                }
            }
            if (oreTotaliSvolte >= 8){
                //Se la giornata ha il permesso dello straodinario faccio aggiungere più ore
                for (Integer straordinario : giornataStrardinario){
                    if (straordinario.equals("0")){
                        log.warning("Ore svolte maggiori di 8");
                        throw new IllegalArgumentException("Ore svolte maggiori di 8");
                    }
                }
            }
        }

        if (presenza.getStato().equals(StatoPresenza.PERMESSO)){
            try {
                addPresenzaConPermesso(presenza, principal);
            }catch (DataFormatException ex){
                throw ex;
            }
        }
        //verifo l'orario nel caso in cui si tratti dello stato "PRESENTE"
        if (oraEntrata.isAfter(oraUscita)){
            log.warning("Ora uscita più grande dell'oraEntrata");
            throw new DataFormatException("Ora uscita più grande dell'oraEntrata");
        }
        presenza.setDipendente(dipendente);
        return presenzaRepository.save(presenza);
    }

    public void addPresenzaConFerie(Presenza presenza, Principal principal) throws IllegalArgumentException, ArithmeticException {
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = utente.getDipendente();
        LocalDate inizioFerie = presenza.getDataInizioFerie();
        LocalDate fineFerie = presenza.getDataFineFerie();

        if (inizioFerie.isAfter(fineFerie)){
            log.warning("Data inizio superiore a quella di fine");
            throw new IllegalArgumentException("Data inizio superiore a quella di fine");
        }

        for (LocalDate i = inizioFerie; !i.isAfter(fineFerie); i = i.plusDays(1)) {
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
        LocalTime oraEntrata = presenza.getOraEntrata();
        LocalTime oraUscita = presenza.getOraUscita();

        if (oraEntrata.isAfter(oraUscita)){
            log.warning("Ora uscita più grande dell'oraEntrata");
            throw new DataFormatException("Ora uscita più grande dell'oraEntrata");
        }

        //Verifico se esiste un contratto per il dipendente
        Optional<Contratto> contrattoDipendente = contrattoRepository.findBydipendente(dipendente);
        if(contrattoDipendente.isPresent()){
            //Restituisce il totale dei minuti
            Duration durataTotalePermesso = Duration.between(oraEntrata,oraUscita);
            // Converto la durata divindendo per 60 per prendermi le ore e metterlo in float
            float oreTotaliPermessoConvertitoInFloat = durataTotalePermesso.toMinutes() / 60.0f;
            contrattoDipendente.get().setOreFerieUtilizzate(contrattoDipendente.get().getOreFerieUtilizzate() + oreTotaliPermessoConvertitoInFloat);
            Contratto contrattoDipendenteEsistente = contrattoDipendente.get();
            contrattoRepository.save(contrattoDipendenteEsistente);
        }else {
            log.warning("Contratto non presente per il dipendente");
            throw new IllegalArgumentException("Contratto non presente per il dipendente");
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
        //presenti più date uguali
        List<Presenza> presenzaList = presenzaRepository.findByDataList(presenza.getData());

        if (presenza.isChiudiGiornata()){
            for (int i = 0; i < presenzaList.size(); i ++){
                Presenza presenzaRecuperata = presenzaList.get(i);
                presenzaList.get(i).setChiudiGiornata(false);
                presenzaRepository.save(presenzaRecuperata);
                if (i == presenzaList.size()-1){
                    return false;
                }
            }
        }

        if (presenza.getStato().equals(StatoPresenza.FERIE)){
            presenza.setChiudiGiornata(true);
            presenzaRepository.save(presenza);
            return true;
        }

        //Più date uguali
        float durataTotaleGiornata = 0;
        for (Presenza presenzePerData : presenzaList){
            LocalTime oraEntrata = presenzePerData.getOraEntrata();
            LocalTime oraUscita = presenzePerData.getOraUscita();
            Duration durataGionarta = Duration.between(oraEntrata,oraUscita);
            durataTotaleGiornata += durataGionarta.toHours();
        }
        if (durataTotaleGiornata >= 8){
            for (int i = 0; i<presenzaList.size(); i ++){
                presenzaList.get(i).setChiudiGiornata(true);
                presenzaRepository.save(presenzaList.get(i));
                if (i == presenzaList.size()-1){
                    return true;
                }
            }
        }else {
            log.warning("Giornate non chiudibili, meno di 8 ore fatte!!");
            throw new ArithmeticException("Giornata non chiudibile");
        }
        return false;

    }

    public void inserisciFilePresenze(MultipartFile file, Principal principal) throws IOException {
        //Recupero il Dipendente

        List<Presenza> presenzaList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        //Salto la prima riga
        reader.readLine();

        //Leggiamo file fino a quando la riga non sarà null
        while ((line = reader.readLine()) != null){
            String[] parts = line.split(",");
            //Almeno tre elementi nel file
            if (parts.length > 3){
                Presenza presenza = new Presenza();

                //Verifico se la datà è già presente a DB
                LocalDate data = LocalDate.parse(parts[0]);
                Optional<Presenza> presenzaGiornata = presenzaRepository.findByData(data);
                if (presenzaGiornata.isPresent()){
                    log.warning("Giornata già presente a DB " + data);
                    throw new IOException("Giornata già presente");
                }
                presenza.setData(data);

                //Verifico la modalità
                String modalita = parts[1];
                if (!modalita.equals("UFFICIO") && !modalita.equals("FUORISEDE")
                        && !modalita.equals("SMARTWORKING")){
                    log.warning("Modalità inserita errata");
                    throw new IOException("Modalità inserita errata");
                }
                presenza.setModalita(parts[1]);

                //Verifico stato
                String stato = parts[2];
                if (!isValido(stato)) {
                    log.warning("Stato inserito non valido: " + stato);
                    throw new IOException("Stato inserito non valido");
                }
                StatoPresenza statoPresenzaEnum = StatoPresenza.valueOf(stato);
                presenza.setStato(statoPresenzaEnum);

                //Caso in cui vengano inserite ferie
                if (statoPresenzaEnum.equals(StatoPresenza.FERIE)){
                    presenza.setOraEntrata(null);
                    presenza.setOraUscita(null);

                    LocalDate dataInizioFerie = LocalDate.parse(parts[6]);
                    LocalDate dataFineFerie = LocalDate.parse(parts[5]);
                    if (dataInizioFerie.isAfter(dataFineFerie)){
                        log.warning("Date ferie inserite non valide");
                        throw new IOException("Date ferie inserite non valide");
                    }else {
                        presenza.setDataInizioFerie(dataInizioFerie);
                        presenza.setDataFineFerie(dataFineFerie);
                    }
                }
                //Caso in cui venga inserite Permesso
                if (statoPresenzaEnum.equals(StatoPresenza.PERMESSO) || statoPresenzaEnum.equals(StatoPresenza.PRESENTE)){
                    presenza.setDataInizioFerie(null);
                    presenza.setDataFineFerie(null);

                    LocalTime oraEntrata = LocalTime.parse(parts[3]);
                    LocalTime oraUscita = LocalTime.parse(parts[4]);
                    if (oraEntrata.isAfter(oraUscita)){
                        log.warning("Ora entrata e ora uscita non valida");
                        throw new IOException("Ora entrata e ora uscita non valida");
                    }else {
                        presenza.setOraEntrata(oraEntrata);
                        presenza.setOraUscita(oraUscita);
                    }

                }
                presenza.setDipendente(loginService.recuperoDipendente(principal));
                String chiudiGiornata = parts[7];
                if (chiudiGiornata.isEmpty()){
                    presenza.setChiudiGiornata(Boolean.FALSE);
                }else if (chiudiGiornata.equals("1")){
                    presenza.setChiudiGiornata(Boolean.TRUE);
                }else {
                    presenza.setChiudiGiornata(Boolean.FALSE);
                }

                presenzaRepository.save(presenza);
            }
        }
    }

    private boolean isValido(String stato) {
        return stato.equals("PERMESSO") || stato.equals("PRESENTE") || stato.equals("FERIE");
    }
}
