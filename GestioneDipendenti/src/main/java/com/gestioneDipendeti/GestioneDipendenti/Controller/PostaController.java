package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Assistenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AssistenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import com.gestioneDipendeti.GestioneDipendenti.Service.PostaService;
import com.gestioneDipendeti.GestioneDipendenti.Service.PresenzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

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

    @Autowired
    private PresenzaService presenzaService;

    @GetMapping()
    public String showPostaPage(Model model, Principal principal){
        Utente utente = loginService.recuperoUtente(principal);
        Optional<Utente> utenteConRuolo2 = utenteRepository.findUtenteConSoloRuolo2(utente.getIdUtente());
        if (utenteConRuolo2.isEmpty()){
            model.addAttribute("listPosta",assistenzaRepository.findByUtente_IdUtenteOrderByIdAssistenzaDesc(utente.getIdUtente()));
        }else{
            model.addAttribute("listPosta",assistenzaRepository.findByIdUtenteAperturaOrderByIdAssistenzaDesc(utente.getIdUtente()));
        }
        return "Assistenza/posta";
    }

    @GetMapping("/richiesta/{idRichiesta}")
    public String showSingleRichiesta(@PathVariable("idRichiesta") long idRichiesta, Model model){
        Assistenza assistenza = assistenzaRepository.findById(idRichiesta).get();
        List<Utente> utenti = utenteRepository.findAll();
        //recupero l'utente che ha aperto la richiesta
        long idUtenteAperturaRichiesta = assistenza.getIdUtenteApertura();
        Utente usernameUtenteAperturaRichiesta = utenteRepository.findById(idUtenteAperturaRichiesta).get();
        model.addAttribute("assistenza",assistenza);
        model.addAttribute("utenteList",utenti);
        model.addAttribute("chiudiRichiesta", new Presenza());
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

    @PostMapping("/chiudi-richiesta/{idAssistena}")
    public String chiudiRichiesta(@ModelAttribute("chiudRichiesta")Presenza presenza, @PathVariable("idAssistena") long idAssistenza,
                                  @RequestParam("motivazioneRichiesta") String motivazioneRichiesta,
                                  @RequestParam(value = "username") String username,
                                  @RequestParam(value = "rispostaRichiestaAssistenza", required = false) String rispostaRichiestaAssistenza,
                                  @RequestParam(value = "ferieDecisione",required = false) String ferieDecisione,
                                  @RequestParam(value = "rispostaGenerica", required = false) String rispostaGenerica,
                                  RedirectAttributes redirectAttributes){
        Optional<Utente> utente = utenteRepository.findByUsername(username);
        if (utente.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "Utente indicato non valido");
            return "redirect:/posta/richiesta/" + idAssistenza;
        }

        if (motivazioneRichiesta.equals("FERIE")){
            Assistenza assistenza = assistenzaRepository.findById(idAssistenza).get();
            assistenza.setRispostaAssistenza("Ferie Accettate: " + ferieDecisione + ", " + rispostaRichiestaAssistenza);
            assistenzaRepository.save(assistenza);
            redirectAttributes.addFlashAttribute("successMessage","Risposta aggiunta correttamente");
            return "redirect:/posta/richiesta/" + idAssistenza;
        }

        if (motivazioneRichiesta.equals("RICHIESTA_GENERICA")){
            Assistenza assistenza = assistenzaRepository.findById(idAssistenza).get();
            assistenza.setRispostaAssistenza(rispostaGenerica);
            assistenzaRepository.save(assistenza);
            redirectAttributes.addFlashAttribute("successMessage","Risposta aggiunta correttamente");
            return "redirect:/posta/richiesta/" + idAssistenza;
        }

        //Usiamo lamba(classe anonima) evitiamo di crearci una classe apposta per trasformare stringa --> principal
        Principal principal = () -> utente.get().getUsername();
        try {
            presenzaService.addPreseza(presenza, principal);
            redirectAttributes.addFlashAttribute("successMessage","Presenza aggiunta correttamente");
            return "redirect:/posta/richiesta/" + idAssistenza;
        }catch (IllegalArgumentException e){
            //Riga già presente a db
            redirectAttributes.addFlashAttribute("errorMessage","Per la data indicata non è possibile " +
                    " aggiungere presenze/permessi");
            return "redirect:/posta/richiesta/" + idAssistenza;
        }catch (DataFormatException e){
            //Orario non valido;
            redirectAttributes.addFlashAttribute("errorMessage","Orario non valido");
            return "redirect:/posta/richiesta/" + idAssistenza;
        }
    }
}
