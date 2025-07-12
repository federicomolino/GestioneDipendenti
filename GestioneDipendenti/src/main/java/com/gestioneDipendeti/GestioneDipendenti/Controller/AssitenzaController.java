package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.TipologiaRichiestaAssistenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RoleRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("assistenza")
public class AssitenzaController {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping()
    public String showPageAssistenza(Model model){
        List<Utente> utentiAdmin = utenteRepository.findByRole_IdRole(1L);
        model.addAttribute("listUtentiAdmin", utentiAdmin);
        model.addAttribute("tipologia", TipologiaRichiestaAssistenza.values());
        return "Assistenza/assistenza";
    }

    @PostMapping()
    public String addRichiestaAssistenza(@RequestParam("responsabile") long idUtente,
                                         @RequestParam("tipologiaRichiestaAssistenza") TipologiaRichiestaAssistenza tipologiaRichiestaAssistenza,
                                         @RequestParam("richiesta") String richiesta){
        return "Assistenza/assistenza";
    }
}
