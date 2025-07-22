package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Assistenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AssistenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostaService {

    @Autowired
    private AssistenzaRepository assistenzaRepository;

    @Autowired
    private PresenzaRepository presenzaRepository;

    public void chiudiRichiesta(List<Long> idAssistenzaList){
        for (Long idRichiesta : idAssistenzaList){
            Assistenza assistenza = assistenzaRepository.findById(idRichiesta).get();
            assistenza.setRichiestaChiusa(true);
            assistenzaRepository.save(assistenza);
        }
    }

    public void leggiRichiesteAssistenza(List<Long> idAssistenzaList){
        for (Long idRichiesta : idAssistenzaList){
            Assistenza assistenza = assistenzaRepository.findById(idRichiesta).get();
            assistenza.setComunicazioneLetta(true);
            assistenzaRepository.save(assistenza);
        }
    }

    public void isGiornataStraordinario(List<Presenza> presenzaList)throws IllegalArgumentException{
        if (presenzaList.isEmpty()){
            throw new IllegalArgumentException("Nessuna presenza presente");
        }
        for (Presenza p : presenzaList){
            p.setGiornataStraordinario(true);
            presenzaRepository.save(p);
        }
    }
}
