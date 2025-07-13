package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AssistenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAttributes {
    @Autowired
    private AssistenzaRepository assistenzaRepository;

    @Autowired
    private LoginService loginService;

    @ModelAttribute
    public void aggiungiNumeroPosta(Model model, Principal principal) {
        if (principal != null) {
            Utente user = loginService.recuperoUtente(principal);
            Long numeroPosta = assistenzaRepository.countRichiesteApertePerUtente(user.getIdUtente());
            model.addAttribute("numeroPosta", numeroPosta);
        }
    }
}
