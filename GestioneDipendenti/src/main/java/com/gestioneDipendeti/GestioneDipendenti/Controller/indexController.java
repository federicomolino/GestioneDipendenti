package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.*;
import com.gestioneDipendeti.GestioneDipendenti.Repository.*;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import com.gestioneDipendeti.GestioneDipendenti.Service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
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

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private AutoRepository autoRepository;

    @GetMapping()
    public String showUtente(Model model, Principal principal){
        //Recupero dipendente
        Utente user = loginService.recuperoUtente(principal);
        Long idUtente = user.getIdUtente();
        Dipendente dipendente = dipendenteRepository.findByUtenteId(idUtente);

        //Verifico se il dipendente ha un'auto
        Optional<MacchinaAziendale> auto = autoRepository.findByDipendente_IdDipendente(dipendente.getIdDipendente());
        if (auto.isPresent()){
            model.addAttribute("auto",auto.get());
        }

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

        //Verifico se ci sono contratti in scadenza
        List<Utente> utenti = utenteRepository.findAll();
        utenteService.contrattiScaduti(utenti);
        return "Index/Index";
    }
}
