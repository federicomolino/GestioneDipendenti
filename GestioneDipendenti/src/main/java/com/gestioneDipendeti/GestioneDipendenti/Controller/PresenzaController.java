package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.PresenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import com.gestioneDipendeti.GestioneDipendenti.Service.PresenzaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("presenza")
public class PresenzaController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private PresenzaService presenzaService;

    @Autowired
    private PresenzaRepository presenzaRepository;

    @Autowired
    private ContrattoRepository contrattoRepository;


    @PostMapping("/addPresenza")
    public String addPresenza(@Valid @ModelAttribute("formAddPresenza")Presenza presenza, BindingResult bindingResult,
                              Principal principal, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formAddPresenza", bindingResult);
            redirectAttributes.addFlashAttribute("formAddPresenza", presenza);
            return "redirect:/";
        }

        Dipendente dipendente = loginService.recuperoDipendente(principal);

        //Eventuale aggiunta di ferie
        Presenza presenzaGiornaliera = presenzaRepository.findByPresenza(dipendente);
        if (presenza.getDataInizioFerie() != null && presenza.getDataFineFerie() != null){
            try{
                presenzaService.addPresenzaConFerie(presenza,principal);
                return "redirect:/";
            }catch (ArithmeticException ex){
                redirectAttributes.addFlashAttribute("presenzaError", "Limite ferire superato!!");
                return "redirect:/";
            }catch (Exception e){
                redirectAttributes.addFlashAttribute("presenzaError", "Errore generico durante" +
                        "il tentativo di salvataggio");
                    return "redirect:/";
            }
        }

        if (presenzaGiornaliera != null){
            redirectAttributes.addFlashAttribute("presenzaError","Timbratura già effettuata");
            return "redirect:/";
        }

        if (!presenza.getData().equals(LocalDate.now())){
            redirectAttributes.addFlashAttribute("dataError","Data inserita non valida");
            return "redirect:/";
        }

        if (presenza.getOraEntrata() == null){
            redirectAttributes.addFlashAttribute("OraEntrataError","Orario inserito non valido");
            return "redirect:/";
        } else if (presenza.getOraUscita() == null) {
            redirectAttributes.addFlashAttribute("OraUscitaError","Orario inserito non valido");
            return "redirect:/";
        }

        if (!presenza.getModalita().equals("UFFICIO") &&
            !presenza.getModalita().equals("SMARTWORKING") &&
            !presenza.getModalita().equals("FUORISEDE")){
            redirectAttributes.addFlashAttribute("modalitaError","Modalità inserita non valida");
            return "redirect:/";
        }

        //Mi salvo la presenza
        presenzaService.addPreseza(presenza,principal);
        redirectAttributes.addFlashAttribute("presenzaSuccess","Presenza Salvata con successo");
        return "redirect:/";
    }

    @GetMapping("/calendar")
    public String showCalendar(@RequestParam(name = "ricercaData", required = false) String ricercaData,
                               Model model, Principal principal, RedirectAttributes redirectAttributes){
        Dipendente dipendente = loginService.recuperoDipendente(principal);
        Optional<Contratto> contratto = contrattoRepository.findBydipendente(dipendente);
        if (!contratto.isPresent()){
            redirectAttributes.addFlashAttribute("presenzaError","L'utente non ha un contratto");
            return "redirect:/";
        }
        int oreFerieRimanenti = contratto.get().getOreFerieTotali() - contratto.get().getOreFerieUtilizzate();
        if (ricercaData == null || ricercaData.isEmpty()){
            model.addAttribute("listaPresenze", presenzaRepository.findByListPrenseza(dipendente));
            model.addAttribute("oreFerieRimanenti",oreFerieRimanenti);
        }else {
            model.addAttribute("listaPresenze", presenzaRepository.findBySearchData(dipendente,ricercaData));
            model.addAttribute("oreFerieRimanenti",oreFerieRimanenti);
        }
        return "Calendar/Calendar";
    }

    @GetMapping("/calendar/{idPresenza}")
    public String showPageEditDay(@PathVariable("idPresenza") Long idPresenza, Model model){
        Presenza presenza = presenzaRepository.findById(idPresenza).get();
        model.addAttribute("formEditDay", presenza);
        return "Calendar/EditDayCalendar";
    }

    @PostMapping("/calendar/{idPresenza}")
    public String editDay(@Valid @ModelAttribute("formEditDay") Presenza formEditDay, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nella modifica dei dati");
            return "redirect:/presenza/calendar/" + formEditDay.getIdPresenza();
        }

        //modifica la presenza
        presenzaService.editPresenza(formEditDay);
        redirectAttributes.addFlashAttribute("successMessage", "Modifica Effettuata");
        return "redirect:/presenza/calendar/" + formEditDay.getIdPresenza();
    }
}
