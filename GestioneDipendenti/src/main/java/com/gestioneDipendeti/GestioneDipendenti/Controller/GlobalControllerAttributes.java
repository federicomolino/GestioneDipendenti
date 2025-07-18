package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AssistenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAttributes {
    @Autowired
    private AssistenzaRepository assistenzaRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UtenteRepository utenteRepository;

    @ModelAttribute
    public void aggiungiNumeroPosta(Model model, Principal principal) {
        if (principal != null) {
            Utente user = loginService.recuperoUtente(principal);
            Optional<Utente> utente = utenteRepository.findUtenteConSoloRuolo2(user.getIdUtente());
            if (utente.isEmpty()){
                Long numeroPosta = assistenzaRepository.countRichiesteApertePerUtente(user.getIdUtente());
                model.addAttribute("numeroPosta", numeroPosta);
            }else{
                int numeroPosta = assistenzaRepository.
                        countByIdUtenteAperturaAndRispostaAssistenzaIsNotNullAndComunicazioneLettaFalse(utente.get().getIdUtente());
                model.addAttribute("numeroPosta", numeroPosta);
            }
        }
    }
}
