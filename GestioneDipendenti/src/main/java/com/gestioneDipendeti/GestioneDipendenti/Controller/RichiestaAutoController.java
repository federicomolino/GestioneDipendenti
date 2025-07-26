package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.MacchinaAziendale;
import com.gestioneDipendeti.GestioneDipendenti.Exception.AutoNonDisponibileException;
import com.gestioneDipendeti.GestioneDipendenti.Exception.AutoPerDipendeteExist;
import com.gestioneDipendeti.GestioneDipendenti.Exception.RuoloContrattoNonValido;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AutoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.AutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller()
@RequestMapping("/richiesta-macchina")
public class RichiestaAutoController {

    private AutoRepository autoRepository;

    private AutoService autoService;

    public RichiestaAutoController(AutoRepository autoRepository,
                                   AutoService autoService){
        this.autoRepository = autoRepository;
        this.autoService = autoService;
    }

    @GetMapping()
    public String showRichiestaAuto(Model model){
        List<MacchinaAziendale> auto = autoRepository.findAll();
        model.addAttribute("listAuto",auto);
        model.addAttribute("formAuto", new MacchinaAziendale());
        return "Auto/RichiestaAuto";
    }

    @PostMapping("/verificaUtente")
    public String ricercaPerDipendete(@RequestParam(name = "username")String username,
                                      RedirectAttributes redirectAttributes){
        try{
            autoService.verificaDipendetePerAuto(username);
            redirectAttributes.addFlashAttribute("successMessage","Hai diritto all'auto");
            return "redirect:/richiesta-macchina";

        } catch (ClassNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage","Non hai nessun contratto");
            return "redirect:/richiesta-macchina";

        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage","Utente inserito non valido");
            return "redirect:/richiesta-macchina";

        }catch (Error e){
            redirectAttributes.addFlashAttribute("errorMessage","Contratto scaduto");
            return "redirect:/richiesta-macchina";
        }catch (RuoloContrattoNonValido ex){
            redirectAttributes.addFlashAttribute("errorMessage",username + " non hai diritto all'auto");
            return "redirect:/richiesta-macchina";
        }catch (AutoPerDipendeteExist ex){
            redirectAttributes.addFlashAttribute("errorMessage",username + " ha già all'auto");
            return "redirect:/richiesta-macchina";
        }
    }

    @PostMapping("/richiestaIdAuto")
    public String richiestaIdAutoController(@RequestParam("idAuto") String idAuto, RedirectAttributes redirectAttributes){
        if (idAuto.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage","idAuto non valido");
            return "redirect:/richiesta-macchina";
        }
        long idAutoInserito = Long.parseLong(idAuto);
        try{
            autoService.richiestaIdAutoService(idAutoInserito);
            redirectAttributes.addFlashAttribute("successMessage","Puoi andare a ritirare l'auto");
            return "redirect:/richiesta-macchina";
        }catch (AutoNonDisponibileException ex){
            redirectAttributes.addFlashAttribute("errorMessage","L'auto non è disponibile");
            return "redirect:/richiesta-macchina";
        }catch (IllegalArgumentException ex){
            redirectAttributes.addFlashAttribute("errorMessage","idAuto non valido");
            return "redirect:/richiesta-macchina";
        }
    }

    @PostMapping("/aggiungiAuto")
    public String aggiungiAuto(@ModelAttribute("formAuto") MacchinaAziendale formAuto, RedirectAttributes redirectAttributes){
        try {
            autoService.aggiungiAuto(formAuto);
            redirectAttributes.addFlashAttribute("successMessage","Auto Aggiunta Correttamente");
            return "redirect:/richiesta-macchina";
        }catch (IllegalArgumentException ex){
            redirectAttributes.addFlashAttribute("errorMessage","Errore nell'inserimento dei dati");
            return "redirect:/richiesta-macchina";
        }
    }

    @PostMapping("/eliminaAuto/{idAuto}")
    public String eliminaAuto(@PathVariable("idAuto")long idAuto, RedirectAttributes redirectAttributes){
        try {
            autoService.cancellaMacchina(idAuto);
            redirectAttributes.addFlashAttribute("successMessage","Auto Cancellata Correttamente");
            return "redirect:/richiesta-macchina";
        }catch (AutoNonDisponibileException ex){
            redirectAttributes.addFlashAttribute("errorMessage","L'auto è assegnata ad un" +
                    " dipendente, impossibile procedere con la cancellazione");
            return "redirect:/richiesta-macchina";
        }
    }
}
