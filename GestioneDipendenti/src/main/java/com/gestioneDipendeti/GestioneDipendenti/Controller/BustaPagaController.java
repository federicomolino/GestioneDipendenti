package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.BustaPaga;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Exception.BustaPagaPresente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.BustaPagaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.InfoBustaPagaService;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("busta")
public class BustaPagaController {

    private BustaPagaRepository bustaPagaRepository;

    private LoginService loginService;

    private DipendenteRepository dipendenteRepository;

    private ContrattoRepository contrattoRepository;

    private InfoBustaPagaService infoBustaPagaService;


    @Autowired
    public BustaPagaController(BustaPagaRepository bustaPagaRepository, LoginService loginService, DipendenteRepository
                               dipendenteRepository, ContrattoRepository contrattoRepository, InfoBustaPagaService
                               infoBustaPagaService){
        this.bustaPagaRepository = bustaPagaRepository;
        this.loginService = loginService;
        this.dipendenteRepository = dipendenteRepository;
        this.contrattoRepository = contrattoRepository;
        this.infoBustaPagaService = infoBustaPagaService;
    }

    @GetMapping
    public String showInfoBustaPaga(@RequestParam(value = "ricercaPerMese", required = false)String mese,
                                    Model model, Principal principal, RedirectAttributes redirectAttributes){
        Dipendente dipendente = loginService.recuperoDipendente(principal);
        if (mese != null){
            BustaPaga b = infoBustaPagaService.ricercaBustaPagaPerMese(mese,dipendente);
            if (b == null){
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Nessuna Busta Paga Presente per il mese "+ mese);
                return "redirect:/busta";
            }else {
                model.addAttribute("bustePaga", b);
            }
        }

        List<BustaPaga> listBustaPagaPerDipendente = bustaPagaRepository.findByDipendente(dipendente);
        if (!listBustaPagaPerDipendente.isEmpty()){
            model.addAttribute("bustePaga", listBustaPagaPerDipendente);
        }
        return "BustaPaga/InfoBustaPaga";
    }

    @PostMapping("/genera/bustePaghe")
    public String generaBustePaghePerDipendenti(RedirectAttributes redirectAttributes){
        List<Dipendente> dipendenti = dipendenteRepository.findAll();
        List<Dipendente> dipendentiConContratto = new ArrayList<>();
        for (Dipendente d : dipendenti){
            Optional<Contratto> contrattoPerDipendente = contrattoRepository.findBydipendente(d);
            if (contrattoPerDipendente.isPresent()){
                dipendentiConContratto.add(d);
            }
        }

        if (dipendentiConContratto.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "Nessun Dipendente ha un contratto," +
                    " impossibile procedere con la generazione");
            return "redirect:/busta";
        }

        try{
            infoBustaPagaService.generaBustePaghe(dipendentiConContratto);
            redirectAttributes.addFlashAttribute("successMessage", "Buste Paghe create con " +
                    "successo");
            return "redirect:/busta";
        }catch (IllegalArgumentException ex){
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante la creazione " +
                    "delle buste paghe");
            return "redirect:/busta";
        }catch (BustaPagaPresente e){
            redirectAttributes.addFlashAttribute("errorMessage", "Buste Paga già presenti");
            return "redirect:/busta";
        }
    }
}
