package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.MacchinaAziendale;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AutoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.AutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        }
    }
}
