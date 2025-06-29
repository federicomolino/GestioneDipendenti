package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipartimento;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipartimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DipartimentoService {

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    public Dipartimento addDipartimento(Dipartimento dipartimento){
        return dipartimentoRepository.save(dipartimento);
    }

}
