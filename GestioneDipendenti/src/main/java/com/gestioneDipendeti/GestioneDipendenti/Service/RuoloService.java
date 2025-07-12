package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipartimento;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Ruolo;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipartimentoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RuoloRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuoloService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    @Autowired
    private RuoloRepository ruoloRepository;

    public Ruolo addRuoloPerDipendente(Ruolo ruolo, Long IdUtente,Long areaFunzionale){
        Utente utente = utenteRepository.findById(IdUtente).get();
        Dipartimento dipartimento = dipartimentoRepository.findById(areaFunzionale).get();
        Ruolo ruoloPerUtente = ruolo;
        ruoloPerUtente.setDipendente(utente.getDipendente());
        ruoloPerUtente.setAreaFunzionale(dipartimento.getDipartimento());
        //aggiungo 1 nuovo dipendente e salvo il dipartimento
        dipartimento.setDipendentiPresenti(dipartimento.getDipendentiPresenti()+1);
        ruoloPerUtente.setDipartimento(dipartimento);
        dipartimentoRepository.save(dipartimento);
        return ruoloRepository.save(ruoloPerUtente);
    }
}
