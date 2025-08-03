package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.BustaPaga;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Exception.BustaPagaPresente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.BustaPagaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import org.springframework.stereotype.Service;

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
}
