package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Assistenza;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AssistenzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostaService {

    @Autowired
    private AssistenzaRepository assistenzaRepository;

    public void chiudiRichiesta(List<Long> idAssistenzaList){
        for (Long idRichiesta : idAssistenzaList){
            Assistenza assistenza = assistenzaRepository.findById(idRichiesta).get();
            assistenza.setRichiestaChiusa(true);
            assistenzaRepository.save(assistenza);
        }
    }
}
