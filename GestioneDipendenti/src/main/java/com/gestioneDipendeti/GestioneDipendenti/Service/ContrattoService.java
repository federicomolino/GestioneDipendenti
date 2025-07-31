package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.tipologiaContratto;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ContrattoService {

    @Autowired
    private ContrattoRepository contrattoRepository;

    public Contratto addContratto(Contratto contratto, Dipendente dipendente) throws IllegalArgumentException{
        if (contratto.getTipologiaContratto().equals(tipologiaContratto.INDETERMINATO)){
            LocalDate dataFine = LocalDate.of(2200,12,31);
            contratto.setDataInzio(LocalDate.now());
            contratto.setDataFine(dataFine);
            contratto.setDipendente(dipendente);
            contratto.setLivelliContrattiCommercio(contratto.getLivelliContrattiCommercio());
            return contrattoRepository.save(contratto);
        }
        if (contratto.getDataInzio().isAfter(contratto.getDataFine())){
            throw new IllegalArgumentException("La data di inizio non può essere superiore a quella di fine");
        }

        if (contratto.getOreSettimanali()<20){
            throw new IllegalArgumentException("Ore minime di 20");
        }

        double stipendioLordoAnnuo = contratto.getStipendioLordo().doubleValue();
        if (stipendioLordoAnnuo <= 100){
            throw new IllegalArgumentException("Minimo 101");
        }

        if (contratto.getOreFerieTotali()<=0){
            throw new IllegalArgumentException("Ore ferie inserite non valide");
        }

        contratto.setDipendente(dipendente);
        contratto.setLivelliContrattiCommercio(contratto.getLivelliContrattiCommercio());
        return contrattoRepository.save(contratto);
    }

    public Contratto editContratto(Contratto contratto, Dipendente dipendente) throws IllegalArgumentException{

        //contratto a tempo indenterminato
        if (contratto.getTipologiaContratto().equals(tipologiaContratto.INDETERMINATO)){
            Contratto contrattoDipendente = contrattoRepository.findBydipendente(dipendente).get();
            LocalDate dataFine = LocalDate.of(2200,12,31);
            contrattoDipendente.setDataInzio(LocalDate.now());
            contrattoDipendente.setTipologiaContratto(contratto.getTipologiaContratto());
            contrattoDipendente.setDataFine(dataFine);
            contrattoDipendente.setDipendente(dipendente);
            contrattoDipendente.setOreSettimanali(contratto.getOreSettimanali());
            contrattoDipendente.setStipendioLordo(contratto.getStipendioLordo());
            contrattoDipendente.setOreFerieTotali(contratto.getOreFerieTotali());
            contrattoDipendente.setOreFerieUtilizzate(contratto.getOreFerieUtilizzate());
            contrattoDipendente.setLivelliContrattiCommercio(contratto.getLivelliContrattiCommercio());
            return contrattoRepository.save(contrattoDipendente);
        }
        if (contratto.getDataInzio().isAfter(contratto.getDataFine())){
            throw new IllegalArgumentException("La data di inizio non può essere superiore a quella di fine");
        }

        if (contratto.getOreSettimanali()<20){
            throw new IllegalArgumentException("Ore minime di 20");
        }

        double stipendioLordoAnnuo = contratto.getStipendioLordo().doubleValue();
        if (stipendioLordoAnnuo <= 100){
            throw new IllegalArgumentException("Minimo 101");
        }

        if (contratto.getOreFerieTotali()<=0){
            throw new IllegalArgumentException("Ore ferie inserite non valide");
        }
        Contratto contrattoDipendente = contrattoRepository.findBydipendente(dipendente).get();
        contrattoDipendente.setDataInzio(contratto.getDataInzio());
        contrattoDipendente.setTipologiaContratto(contratto.getTipologiaContratto());
        contrattoDipendente.setDataFine(contratto.getDataFine());
        contrattoDipendente.setDipendente(dipendente);
        contrattoDipendente.setOreSettimanali(contratto.getOreSettimanali());
        contrattoDipendente.setStipendioLordo(contratto.getStipendioLordo());
        contrattoDipendente.setOreFerieTotali(contratto.getOreFerieTotali());
        contrattoDipendente.setOreFerieUtilizzate(contratto.getOreFerieUtilizzate());
        contrattoDipendente.setLivelliContrattiCommercio(contratto.getLivelliContrattiCommercio());
        return contrattoRepository.save(contrattoDipendente);
    }

    public Contratto aggiornaContratto(Contratto contratto) throws IllegalArgumentException{
        if (contratto.getDataFine().isBefore(contratto.getDataInzio())){
            throw new IllegalArgumentException("Data fine superiore a quella di inizio");
        }
        if (contratto.getDataFine().isEqual(LocalDate.now()) || contratto.getDataFine().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Contratto con scadenza già superata o scadenza oggi");
        }
        Contratto contrattoScaduto = contrattoRepository.findById(contratto.getIdContratto()).get();
        contrattoScaduto.setDataInzio(contratto.getDataInzio());
        contrattoScaduto.setDataFine(contratto.getDataFine());
        contrattoScaduto.setScaduto(false);
        return contrattoRepository.save(contrattoScaduto);
    }

    public void eliminaContratto(long idContratto){
        contrattoRepository.deleteById(idContratto);
    }

}
