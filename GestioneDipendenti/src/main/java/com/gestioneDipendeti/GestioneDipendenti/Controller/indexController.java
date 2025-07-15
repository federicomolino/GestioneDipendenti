package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.*;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class indexController {

    @Autowired
    private DipendenteRepository dipendenteRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PresenzaRepository presenzaRepository;

    @Autowired
    private ContrattoRepository contrattoRepository;

    @Autowired
    private AssistenzaRepository assistenzaRepository;

    @GetMapping()
    public String showUtente(Model model, Principal principal){
        //Recupero dipendente
        Utente user = loginService.recuperoUtente(principal);
        Long idUtente = user.getIdUtente();
        Dipendente dipendente = dipendenteRepository.findById(idUtente).get();

        model.addAttribute("formAddPresenza", new Presenza());
        model.addAttribute("Dipendete", dipendente);
        Presenza p = presenzaRepository.findTopByDipendenteOrderByDataDesc(dipendente);

        if (p == null){
            model.addAttribute("presenzaGiornaliera", null);
        }else if (p.getData().isEqual(LocalDate.now())){
            model.addAttribute("presenzaGiornaliera", p);
        }

        Optional<Contratto> contrattoDipendente = contrattoRepository.findBydipendente(dipendente);

        if (contrattoDipendente.isPresent()){
            Contratto contratto = contrattoDipendente.get();
            float oreFerieRimaste = contrattoDipendente.get().getOreFerieTotali() - contrattoDipendente.get().getOreFerieUtilizzate();
            model.addAttribute("Contratto", contratto);
            model.addAttribute("oreFerieRimaste", oreFerieRimaste);
        }
        return "Index/Index";
    }
}
