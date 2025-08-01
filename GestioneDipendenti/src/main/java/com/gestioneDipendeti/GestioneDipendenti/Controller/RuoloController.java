package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Ruolo;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipartimentoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RuoloRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.RuoloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("ruolo")
public class RuoloController {

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    @Autowired
    private RuoloRepository ruoloRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private RuoloService ruoloService;

    @GetMapping("nuovo-ruolo")
    public String showNuovoRuolo(Model model){
        model.addAttribute("formNewRuolo", new Ruolo());
        model.addAttribute("dipartimenti", dipartimentoRepository.findAll());
        model.addAttribute("utenti", utenteRepository.findAll());
        return "Ruolo/CreaAssegnaRuolo";
    }

    @PostMapping("nuovo-ruolo")
    public String addNuovoRuolo(@Valid @ModelAttribute("formNewRuolo") Ruolo ruolo,
                                BindingResult bindingResult,
                                @RequestParam Map<String, String> associazioniRaw,
                                RedirectAttributes redirectAttributes,
                                Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("dipartimenti", dipartimentoRepository.findAll());
            model.addAttribute("utenti", utenteRepository.findAll());
            return "Ruolo/CreaAssegnaRuolo";
        }

        long idUtente = 0;
        long areaFunzionale = 0;
        for (Map.Entry<String, String> entry : associazioniRaw.entrySet()){
            if (entry.getKey().startsWith("associazioni[")){
                //qualunque carattere che sia tra 0 e 9 passa
                areaFunzionale = Long.parseLong(entry.getKey().replaceAll("[^0-9]",""));
                idUtente = Long.parseLong(entry.getValue());

                System.out.println("Dipartimento " + areaFunzionale + " → Utente " + idUtente);
            }
        }

        try{
            ruoloService.addRuoloPerDipendente(ruolo, idUtente, areaFunzionale);
            redirectAttributes.addFlashAttribute("successMessage","Ruolo creato correttamente!");
        }catch (Exception ex){
            redirectAttributes.addFlashAttribute("errorMessage", "Errore generico durante la" +
                    " creazione, contattare l'assistenza");
        }
        model.addAttribute("dipartimenti", dipartimentoRepository.findAll());
        model.addAttribute("utenti", utenteRepository.findAll());
        return "redirect:/ruolo/nuovo-ruolo";
    }
}
