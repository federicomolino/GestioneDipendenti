package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class loginController {

    @Autowired
    private UtenteRepository utenteRepository;

    @GetMapping()
    public String showLogin(Model model){
        model.addAttribute("UtenteFormLogin", new Utente());
        return "Login/login";
    }
}
