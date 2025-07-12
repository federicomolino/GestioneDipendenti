package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.TipologiaRichiestaAssistenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.AssistenzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.AuthenticationException;
import java.util.List;

@Controller
@RequestMapping("assistenza")
public class AssitenzaController {

    @Autowired
    private UtenteRepository utenteRepository;


    @Autowired
    private AssistenzaService assistenzaService;

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
                                         @RequestParam("richiesta") String richiesta, RedirectAttributes redirectAttributes){

        try {
            assistenzaService.addAsstenza(richiesta,tipologiaRichiestaAssistenza);
            assistenzaService.invioEmailAssistenzaAperta(idUtente,richiesta);
            redirectAttributes.addFlashAttribute("successMessage","Richiesta inoltrata correttamente");
            return "redirect:/assistenza";
        } catch (AuthenticationException ex){
            redirectAttributes.addFlashAttribute("infoMessage","Richiesta assistenza inviata, " +
                    "mail non andata a buon fine");
            return "redirect:/assistenza";
        } catch (Exception ex){
            redirectAttributes.addFlashAttribute("errorMessage","Erroe durante la creazione della richiesta");
            return "redirect:/assistenza";
        }
    }
}
