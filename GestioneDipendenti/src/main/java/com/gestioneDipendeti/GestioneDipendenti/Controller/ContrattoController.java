package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.ContrattoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("utente/utente-contratto")
public class ContrattoController {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    @Autowired
    private ContrattoRepository contrattoRepository;

    @Autowired
    private ContrattoService contrattoService;

    @GetMapping("/contratto/{idUtente}")
    public String showContratto(@PathVariable("idUtente") Long idUtente, Model model){
        Dipendente dipendente = dipendenteRepository.findByUtenteId(idUtente);
        Utente utente = utenteRepository.findById(dipendente.getUtente().getIdUtente()).get();
        model.addAttribute("utente",utente);
        model.addAttribute("formNewContratto", new Contratto());
        return "Contratto/Contratto";
    }

    @PostMapping("/contratto/{idUtente}")
    public String addContrattoPerUtente(@ModelAttribute("formNewContratto") @Valid Contratto contratto,
                                        @PathVariable("idUtente") Long idUtente, BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes, Model model){
        Utente utente = utenteRepository.findById(idUtente).get();
        if (bindingResult.hasErrors()){
            model.addAttribute("utente",utente);
            model.addAttribute("formNewContratto", contratto);
            return "Contratto/Contratto";
        }

        //Verifico se per qull'utente esiste già un contratto
        Dipendente dipendente = dipendenteRepository.findByUtenteId(utente.getIdUtente());
        Optional<Contratto> contrattoPerUtente = contrattoRepository.findBydipendente(dipendente);
        if (contrattoPerUtente.isPresent()){
            redirectAttributes.addFlashAttribute("errorMessage", "Esiste già un contratto" +
                    " per l'utente " + dipendente.getNome() + " " + dipendente.getCognome());
            return "redirect:/utente/utente-contratto/contratto/" + utente.getIdUtente();
        }

        try {
            contrattoService.addContratto(contratto, dipendente);
            redirectAttributes.addFlashAttribute("successMessage","Contratto correttamente creato");
            return "redirect:/utente/utente-contratto/contratto/" + utente.getIdUtente();
        }catch (IllegalArgumentException ex){
            redirectAttributes.addFlashAttribute("errorMessage","Errore durante il salvataggio" +
                    " del contratto");
            return  "redirect:/utente/utente-contratto/contratto/" + utente.getIdUtente();
        }
    }

    @GetMapping("/contratto/edit/{IdUtente}")
    public String showContrattoUtente(@PathVariable("IdUtente") Long idUtente, Model model, RedirectAttributes redirectAttributes){
        Dipendente dipendente = dipendenteRepository.findByUtenteId(idUtente);
        Utente utente = utenteRepository.findById(dipendente.getUtente().getIdUtente()).get();
        Optional<Contratto> contratto = contrattoRepository.findBydipendente(dipendente);
        if (!contratto.isPresent()){
            redirectAttributes.addFlashAttribute("errorMessage","Nessun contratto per il dipendente" +
                    " selezionato");
            return "redirect:/utente/nuovoUtente";
        }
        model.addAttribute("contrattoDi",dipendente.getNome() + " " + dipendente.getCognome());
        model.addAttribute("utente", utente);
        model.addAttribute("formEditContratto",contratto);
        return "Contratto/ContrattoEdit";
    }

    @PostMapping("/contratto/edit/{IdUtente}")
    public String editContratto(@Valid @ModelAttribute("formEditContratto")Contratto contratto,
                                @PathVariable("IdUtente") Long idUtente, RedirectAttributes redirectAttributes,
                                Model model, BindingResult bindingResult){
        Utente utente = utenteRepository.findById(idUtente).get();
        Dipendente dipendente = dipendenteRepository.findByUtenteId(idUtente);
        if (bindingResult.hasErrors()){
            model.addAttribute("contrattoDi",dipendente.getNome() + " " + dipendente.getCognome());
            model.addAttribute("utente", utente);
            model.addAttribute("formEditContratto",contratto);
            return "Contratto/ContrattoEdit";
        }

        try{
            contrattoService.editContratto(contratto,dipendente);
            redirectAttributes.addFlashAttribute("successMessage", "Contratto modificato correttamente");
            return "redirect:/utente/utente-contratto/contratto/edit/" + utente.getIdUtente();
        }catch (IllegalArgumentException ex){
            redirectAttributes.addFlashAttribute("errorMessage","Errore durante il salvataggio" +
                    " del contratto");
            return "redirect:/utente/utente-contratto/contratto/edit/" + utente.getIdUtente();
        }
    }

    @GetMapping("/contratti-scaduti")
    public String contrattiScaduti(Model model){
        List<Contratto> contrattiScaduti = contrattoRepository.findByIsScadutoTrue();

        for (Contratto c : contrattiScaduti){
            Dipendente d = c.getDipendente();
            Utente u = d.getUtente();
            model.addAttribute("username",u.getUsername());
            model.addAttribute("listUtentiContrattoScaduto",contrattiScaduti);
            model.addAttribute("formContratto",c);
        }
        return "Utente/ContrattiScaduti";
    }

    @PostMapping("/contratti-scaduti/aggiorna/{idContratto}")
    public String contrattiScadutiAggiorna(@ModelAttribute("formContratto") Contratto contratto, RedirectAttributes redirectAttributes){
        try {
            contrattoService.aggiornaContratto(contratto);
            redirectAttributes.addFlashAttribute("successMessage","Contratto aggiornato " +
                    "correttamene");
            return "redirect:/utente/utente-contratto/contratti-scaduti";
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage","Errore durante l'aggornamento " +
                    "del contratto, verifica i dati passati");
            return "redirect:/utente/utente-contratto/contratti-scaduti";
        }
    }

    @PostMapping("/contratti-scaduti/elimina/{idContratto}")
    public String eliminaContrattoScaduto(@PathVariable("idContratto") Long idContratto, RedirectAttributes redirectAttributes){
        contrattoService.eliminaContratto(idContratto);
        redirectAttributes.addFlashAttribute("successMessage","Contratto eliminato " +
                "correttamene");
        return "redirect:/utente/utente-contratto/contratti-scaduti";
    }
}
