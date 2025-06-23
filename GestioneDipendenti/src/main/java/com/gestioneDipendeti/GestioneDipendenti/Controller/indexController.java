package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

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

    @GetMapping()
    public String showUtente(Model model, Principal principal){
        //Recupero dipendente
        Utente user = loginService.recuperoUtente(principal);
        Long idUtente = user.getIdUtente();
        Dipendente dipendente = dipendenteRepository.findById(idUtente).get();

        model.addAttribute("formAddPresenza", new Presenza());
        model.addAttribute("Dipendete", dipendente);
        model.addAttribute("presenzaGiornaliera", presenzaRepository.findByPresenza(dipendente));
        return "Index/Index";
    }
}
