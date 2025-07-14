package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Assistenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AssistenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import com.gestioneDipendeti.GestioneDipendenti.Service.PostaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/posta")
public class PostaController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AssistenzaRepository assistenzaRepository;

    @Autowired
    private PostaService postaService;

    @Autowired
    private UtenteRepository utenteRepository;

    @GetMapping()
    public String showPostaPage(Model model, Principal principal){
        Utente utente = loginService.recuperoUtente(principal);
        model.addAttribute("listPosta",assistenzaRepository.findByUtente_IdUtente(utente.getIdUtente()));
        return "Assistenza/posta";
    }

    @GetMapping("/richiesta/{idRichiesta}")
    public String showSingleRichiesta(@PathVariable("idRichiesta") long idRichiesta, Model model){
        Assistenza assistenza = assistenzaRepository.findById(idRichiesta).get();
        //recupero l'utente che ha aperto la richiesta
        long idUtenteAperturaRichiesta = assistenza.getIdUteneApertura();
        Utente usernameUtenteAperturaRichiesta = utenteRepository.findById(idUtenteAperturaRichiesta).get();
        model.addAttribute("assistenza",assistenza);
        model.addAttribute("usernameUtenteAperturaRichiesta", usernameUtenteAperturaRichiesta.getUsername());
        return "Assistenza/leggiRichiesta";
    }

    @PostMapping()
    public String chiudiRichiesteList(@RequestParam(name = "selezionati",required = false) List<Long> selezionatiIdAssistenza,
                                      RedirectAttributes redirectAttributes){
        if (selezionatiIdAssistenza == null || selezionatiIdAssistenza.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage","Non è stata selezionata nessuna" +
                    " richiesta");
            return "redirect:/posta";
        }
        postaService.chiudiRichiesta(selezionatiIdAssistenza);
        redirectAttributes.addFlashAttribute("successMessage","Le richieste sono state chiuse" +
                " con successo");
        return "redirect:/posta";
    }

    @PostMapping("/richiesta/{idAssistenza}")
    public String chiudiRichiesta(@PathVariable("idAssistenza") long idAssistenza, RedirectAttributes redirectAttributes){
        List<Long> idAssistenzaPassato = new ArrayList<>();
        idAssistenzaPassato.add(idAssistenza);
        postaService.chiudiRichiesta(idAssistenzaPassato);
        redirectAttributes.addFlashAttribute("successMessage","La richiesta è stata chiusa" +
                " con successo");
        return "redirect:/posta";
    }
}
