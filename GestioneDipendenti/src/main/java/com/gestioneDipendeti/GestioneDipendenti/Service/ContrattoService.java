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
        return contrattoRepository.save(contratto);
    }
}
