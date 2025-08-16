package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.BustaPaga;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Exception.BustaPagaPresente;
import com.gestioneDipendeti.GestioneDipendenti.Exception.MeseNonCompletoError;
import com.gestioneDipendeti.GestioneDipendenti.Repository.BustaPagaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class InfoBustaPagaService {

    private PresenzaRepository presenzaRepository;

    private ContrattoRepository contrattoRepository;

    private BustaPagaRepository bustaPagaRepository;

    private final Logger logInfoBustaPaga = Logger.getLogger(InfoBustaPagaService.class.getName());

    public InfoBustaPagaService(PresenzaRepository presenzaRepository, ContrattoRepository contrattoRepository,
                                BustaPagaRepository bustaPagaRepository){
        this.presenzaRepository = presenzaRepository;
        this.contrattoRepository = contrattoRepository;
        this.bustaPagaRepository = bustaPagaRepository;
    }

    public void generaBustePaghe(List<Dipendente>dipendentiConContratto) throws IllegalArgumentException, BustaPagaPresente{
         LocalDate data = LocalDate.now().minusMonths(1);
         String dataString = data.toString().substring(0, 7); // "2025-08"
        for (Dipendente d : dipendentiConContratto){
            if (verificaCompletezzaMese(d, null)){
                int numeroGiornateStraordinario = 0;
                List<Presenza> presenzaPerDipendenteEMese = presenzaRepository.findByMeseLike(d,dataString);
                for (int p = 0; p < presenzaPerDipendenteEMese.size(); p++){
                    //Se la giornata è straordinario aumento di 1 il count
                    if (presenzaPerDipendenteEMese.get(p).isGiornataStraordinario()){
                        numeroGiornateStraordinario += 1;
                    }
                }
                //Mi calcolo e salvo lo stipendio
                try {
                    calcolaBustaStipendio(data,numeroGiornateStraordinario, d);
                }catch (IllegalArgumentException ex){
                    logInfoBustaPaga.warning("Errore durante la creazione della busta paga");
                    throw new IllegalArgumentException("Eccezione generica");
                }catch (BustaPagaPresente e){
                    logInfoBustaPaga.warning("Busta paga già presente");
                    throw new BustaPagaPresente("Busta paga già presente");
                }
            }
        }
    }

    private BustaPaga calcolaBustaStipendio(LocalDate data,int numeroGiornateStraordinario,Dipendente dipendente)throws IllegalArgumentException,
            BustaPagaPresente{
        //Formatto la data e verifico se le buste paghe per il mese passato sono state già create
        String dataFormat = data.toString().substring(0,7);
        List<BustaPaga> bp = bustaPagaRepository.findByMeseLike(dataFormat);
        if (!bp.isEmpty()){
            logInfoBustaPaga.warning("Buste paghe già presenti per il mese " + dataFormat);
            throw new BustaPagaPresente("Buste paghe già presenti");
        }

        Contratto c = contrattoRepository.findBydipendente(dipendente).get();
        try{
            BustaPaga bustaPagaPerMese = new BustaPaga();
            bustaPagaPerMese.setMese(data);
            bustaPagaPerMese.setDipendente(dipendente);
            bustaPagaPerMese.setNumeroStraordinariPerMese(numeroGiornateStraordinario);
            bustaPagaPerMese.setTrattenuta(200);
            //Calcolo Stipendio lordo
            float stipendioLordoMensile = c.getStipendioLordo().floatValue() / 14;
            bustaPagaPerMese.setStipendioLordo(stipendioLordoMensile);
            //Calcolo stipendio Netto mensile
            float stipendioNettoMensile = stipendioLordoMensile - bustaPagaPerMese.getTrattenuta();

            //Verifico se ci sono straordinari da calcolare
            int numeroStraordinari = bustaPagaPerMese.getNumeroStraordinariPerMese();
            if (numeroStraordinari > 0){
                float importoDaAggiungereNetto = numeroStraordinari * 50;
                stipendioNettoMensile += importoDaAggiungereNetto;
                bustaPagaPerMese.setStipendioNetto(stipendioNettoMensile);
            }else {
                bustaPagaPerMese.setStipendioNetto(stipendioNettoMensile);
            }
            logInfoBustaPaga.info("Busta Paga Creata con Successo");
            return bustaPagaRepository.save(bustaPagaPerMese);
        }catch (Exception ex){
            logInfoBustaPaga.warning("Errore durante la creazione della busta paga per il dipendente con ID: " +
                    dipendente.getIdDipendente());
            throw new IllegalArgumentException("Eccezione generica");
        }
    }

    public BustaPaga ricercaBustaPagaPerMese(String mese, Dipendente dipendente){
        Optional<BustaPaga> bustaPagaPerMese = bustaPagaRepository.findByMeseLikeAndDipendente(dipendente,mese);
        if (bustaPagaPerMese.isPresent()){
            return bustaPagaPerMese.get();
        }else {
            return null;
        }
    }

    private boolean verificaCompletezzaMese(Dipendente dipendente, String dataMese){
        if (dataMese == null){
            LocalDate data = LocalDate.now();
            LocalDate primoDelMese = data.withDayOfMonth(1);
            int giorniNelMese = data.lengthOfMonth();


            int countNumeroGiornateLavorativeMese = 0;
            for(int i = 0; i < giorniNelMese; i++){
                LocalDate giorno = primoDelMese.plusDays(i);
                DayOfWeek dow = giorno.getDayOfWeek();

                if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY){
                    countNumeroGiornateLavorativeMese ++;
                }
            }

            String dataForm = data.toString().substring(0,7);
            List<Presenza> p = presenzaRepository.findByMeseLike(dipendente,dataForm);
            int countGiornateTimbrate = p.size() +1;
            //Verifica chiusura giornata
            for (Presenza pre : p){
                if (!pre.isChiudiGiornata()){
                    logInfoBustaPaga.warning("Giornata non chiusa per il dipendente con ID: " + dipendente.getIdDipendente() +
                            " per la giornata " + pre.getData());
                    return false;
                }
            }
            //Se torna true le giornate timbrate corrispondono correttamente se no manca qualcosa
            if (countGiornateTimbrate == countNumeroGiornateLavorativeMese){
                return true;
            }else {
                logInfoBustaPaga.warning("Giornate timbrate != da giornate mensili per dipendente con ID: "
                        +dipendente.getIdDipendente() );
                return false;
            }
        }else {
            LocalDate data = LocalDate.parse(dataMese + "-01");
            LocalDate primoDelMese = data.withDayOfMonth(1);
            int giorniNelMese = data.lengthOfMonth();


            int countNumeroGiornateLavorativeMese = 0;
            for(int i = 0; i < giorniNelMese; i++){
                LocalDate giorno = primoDelMese.plusDays(i);
                DayOfWeek dow = giorno.getDayOfWeek();

                if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY){
                    countNumeroGiornateLavorativeMese ++;
                }
            }

            String dataForm = data.toString().substring(0,7);
            List<Presenza> p = presenzaRepository.findByMeseLike(dipendente,dataForm);
            int countGiornateTimbrate = p.size();
            //Verifica chiusura giornata
            for (Presenza pre : p){
                if (!pre.isChiudiGiornata()){
                    logInfoBustaPaga.warning("Giornata non chiusa per il dipendente con ID: " + dipendente.getIdDipendente() +
                            " per la giornata " + pre.getData());
                    return false;
                }
            }
            //Se torna true le giornate timbrate corrispondono correttamente se no manca qualcosa
            if (countGiornateTimbrate == countNumeroGiornateLavorativeMese){
                return true;
            }else {
                logInfoBustaPaga.warning("Giornate timbrate != da giornate mensili per dipendente con ID: "
                        +dipendente.getIdDipendente() );
                return false;
            }
        }
    }

    public BustaPaga generaSingolaBustaPagaPerDipendente(Dipendente dipendente)throws BustaPagaPresente, ArithmeticException,
            IllegalArgumentException,MeseNonCompletoError{
        //Verifico se la busta Paga per il mese è presente
        Optional<Contratto> c = contrattoRepository.findBydipendente(dipendente);
        if(c.isPresent()){
            String dataMensile = LocalDate.now().toString();
            String dataFormattata = dataMensile.substring(0,7);
            if (ricercaBustaPagaPerMese(dataFormattata, dipendente) != null){
                logInfoBustaPaga.warning("Busta paga già presente per il dipendente con Id " + dipendente.getIdDipendente()
                + " e il mese " + dataMensile );
                throw new BustaPagaPresente("Busta paga già presente");
            }else {
                if (verificaCompletezzaMese(dipendente,null)){
                    try{
                        BustaPaga bustaPagaPerMese = new BustaPaga();
                        LocalDate data = LocalDate.parse(dataMensile);
                        bustaPagaPerMese.setMese(data);
                        bustaPagaPerMese.setDipendente(dipendente);
                        //Mi prendo le giornate di straordinarie fatte nel mese dal singolo dipendente e le
                        //aggiungo
                        int numeroGiornateStraordinario = 0;
                        List<Presenza> presenzaPerDipendenteEMese = presenzaRepository.findByMeseLike(dipendente,dataMensile);
                        for (int p = 0; p < presenzaPerDipendenteEMese.size(); p++){
                            //Se la giornata è straordinario aumento di 1 il count
                            if (presenzaPerDipendenteEMese.get(p).isGiornataStraordinario()){
                                numeroGiornateStraordinario += 1;
                            }
                        }
                        bustaPagaPerMese.setNumeroStraordinariPerMese(numeroGiornateStraordinario);
                        bustaPagaPerMese.setTrattenuta(200);
                        //Calcolo Stipendio lordo
                        float stipendioLordoMensile = c.get().getStipendioLordo().floatValue() / 14;
                        bustaPagaPerMese.setStipendioLordo(stipendioLordoMensile);
                        //Calcolo stipendio Netto mensile
                        float stipendioNettoMensile = stipendioLordoMensile - bustaPagaPerMese.getTrattenuta();

                        //Verifico se ci sono straordinari da calcolare
                        int numeroStraordinari = bustaPagaPerMese.getNumeroStraordinariPerMese();
                        if (numeroStraordinari > 0){
                            float importoDaAggiungereNetto = numeroStraordinari * 50;
                            stipendioNettoMensile += importoDaAggiungereNetto;
                            bustaPagaPerMese.setStipendioNetto(stipendioNettoMensile);
                        }else {
                            bustaPagaPerMese.setStipendioNetto(stipendioNettoMensile);
                        }
                        logInfoBustaPaga.info("Busta Paga Creata con Successo");
                        return bustaPagaRepository.save(bustaPagaPerMese);
                    }catch (Exception ex){
                        logInfoBustaPaga.warning("Errore durante la creazione della busta paga per il dipendnete " +
                                dipendente.getIdDipendente());
                        throw  new ArithmeticException("Errore durante la creazione della busta paga");
                    }
                }else {
                    logInfoBustaPaga.warning("Mese non completo di timbrature");
                    throw new MeseNonCompletoError("Mese non completo");
                }
            }
        }else {
            logInfoBustaPaga.warning("Contratto non presente per il dipendente con Id" + dipendente.getIdDipendente());
            throw new IllegalArgumentException("Contratto non presente per il dipendente");
        }
    }

    public BustaPaga generaBustaPagaPerDipendetiConData(Dipendente dipendente, String data)throws BustaPagaPresente,
            MeseNonCompletoError,ArithmeticException{
        //Verifico se la busta paga è già stata generata
        Optional<Contratto> c = contrattoRepository.findBydipendente(dipendente);
        if (c.isPresent()){
            if(ricercaBustaPagaPerMese(data,dipendente) == null){
                //Verifico il mese se è ok
                if (verificaCompletezzaMese(dipendente,data)){
                    try{
                        BustaPaga bustaPagaPerMese = new BustaPaga();
                        LocalDate d = LocalDate.parse(data + "-01");
                        bustaPagaPerMese.setMese(d);
                        bustaPagaPerMese.setDipendente(dipendente);
                        //Mi prendo le giornate di straordinarie fatte nel mese dal singolo dipendente e le
                        //aggiungo
                        int numeroGiornateStraordinario = 0;
                        List<Presenza> presenzaPerDipendenteEMese = presenzaRepository.findByMeseLike(dipendente,data);
                        for (int p = 0; p < presenzaPerDipendenteEMese.size(); p++){
                            //Se la giornata è straordinario aumento di 1 il count
                            if (presenzaPerDipendenteEMese.get(p).isGiornataStraordinario()){
                                numeroGiornateStraordinario += 1;
                            }
                        }
                        bustaPagaPerMese.setNumeroStraordinariPerMese(numeroGiornateStraordinario);
                        bustaPagaPerMese.setTrattenuta(200);
                        //Calcolo Stipendio lordo
                        float stipendioLordoMensile = c.get().getStipendioLordo().floatValue() / 14;
                        bustaPagaPerMese.setStipendioLordo(stipendioLordoMensile);
                        //Calcolo stipendio Netto mensile
                        float stipendioNettoMensile = stipendioLordoMensile - bustaPagaPerMese.getTrattenuta();

                        //Verifico se ci sono straordinari da calcolare
                        int numeroStraordinari = bustaPagaPerMese.getNumeroStraordinariPerMese();
                        if (numeroStraordinari > 0){
                            float importoDaAggiungereNetto = numeroStraordinari * 50;
                            stipendioNettoMensile += importoDaAggiungereNetto;
                            bustaPagaPerMese.setStipendioNetto(stipendioNettoMensile);
                        }else {
                            bustaPagaPerMese.setStipendioNetto(stipendioNettoMensile);
                        }
                        logInfoBustaPaga.info("Busta Paga Creata con Successo");
                        return bustaPagaRepository.save(bustaPagaPerMese);
                    }catch (Exception ex){
                        logInfoBustaPaga.warning("Errore durante la creazione della busta paga per il dipendnete " +
                                dipendente.getIdDipendente());
                        throw  new ArithmeticException("Errore durante la creazione della busta paga");
                    }
                }else {
                    logInfoBustaPaga.warning("Errore durante la verifica della completezza del mese");
                    throw new MeseNonCompletoError("Errore durante la verifica della completezza del mese");
                }
            }else {
                logInfoBustaPaga.warning("Busta Paga del mese indicato già generata");
                throw new BustaPagaPresente("Busta Paga già presente");
            }
        }
        return null;
    }
}
