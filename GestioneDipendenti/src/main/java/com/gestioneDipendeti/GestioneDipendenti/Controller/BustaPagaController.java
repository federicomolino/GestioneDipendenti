package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.BustaPaga;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Ruolo;
import com.gestioneDipendeti.GestioneDipendenti.Exception.BustaPagaPresente;
import com.gestioneDipendeti.GestioneDipendenti.Exception.MeseNonCompletoError;
import com.gestioneDipendeti.GestioneDipendenti.Repository.BustaPagaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RuoloRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.InfoBustaPagaService;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("busta")
public class BustaPagaController {

    private BustaPagaRepository bustaPagaRepository;

    private LoginService loginService;

    private DipendenteRepository dipendenteRepository;

    private ContrattoRepository contrattoRepository;

    private InfoBustaPagaService infoBustaPagaService;

    private RuoloRepository ruoloRepository;


    @Autowired
    public BustaPagaController(BustaPagaRepository bustaPagaRepository, LoginService loginService, DipendenteRepository
                               dipendenteRepository, ContrattoRepository contrattoRepository, InfoBustaPagaService
                               infoBustaPagaService, RuoloRepository ruoloRepository){
        this.bustaPagaRepository = bustaPagaRepository;
        this.loginService = loginService;
        this.dipendenteRepository = dipendenteRepository;
        this.contrattoRepository = contrattoRepository;
        this.infoBustaPagaService = infoBustaPagaService;
        this.ruoloRepository = ruoloRepository;
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
                return "BustaPaga/InfoBustaPaga";
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

    @PostMapping("/genera/bustaPaga")
    public String generaBustaPagaMensile(RedirectAttributes redirectAttributes, Principal principal){
        Dipendente d = loginService.recuperoDipendente(principal);
        try {
            infoBustaPagaService.generaSingolaBustaPagaPerDipendente(d);
            redirectAttributes.addFlashAttribute("successMessage", "Busta paga creata correttamente");
        }catch (BustaPagaPresente bp){
            redirectAttributes.addFlashAttribute("errorMessage", "Busta paga già presente");
        }catch (ArithmeticException ex){
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Errore durante la creazione della busta paga");
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Contratto non presente per il dipendente");
        }catch (MeseNonCompletoError ex){
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mese non completo o chiusura giornate incompleta");
        }
        return "redirect:/busta";
    }

    @GetMapping("/stampa/{idBustaPaga}")
    public String showBustaPaga(@PathVariable("idBustaPaga")long idBustaPaga, Model model){
        Optional<BustaPaga> bustaPagaId = bustaPagaRepository.findById(idBustaPaga);
        if (bustaPagaId.isPresent()){
            //Mi prendo la data e passo al front il nome del mese
            String meseFormattato = bustaPagaId.get().getMese().getMonth().
                    getDisplayName(TextStyle.FULL, Locale.ITALIAN).toUpperCase();
            //Prendo dati del dipendente
            Optional<Dipendente> dipendente = dipendenteRepository.findById(bustaPagaId.get().getDipendente().getIdDipendente());
            if (dipendente.isPresent()){
                Optional<Ruolo> ruoloDipendente = ruoloRepository.findByDipendente(dipendente.get());
                if (ruoloDipendente.isPresent()){
                    model.addAttribute("bustaPaga",bustaPagaId.get());
                    model.addAttribute("dipendente",dipendente.get());
                    model.addAttribute("ruolo",ruoloDipendente.get());
                    model.addAttribute("meseFormattato",meseFormattato);
                    return "BustaPaga/BustaPagaModel";
                }else {
                    model.addAttribute("bustaPaga",bustaPagaId.get());
                    model.addAttribute("dipendente",dipendente.get());
                    model.addAttribute("ruolo",null);
                    model.addAttribute("meseFormattato",meseFormattato);
                    return "BustaPaga/BustaPagaModel";
                }
            }
        }
        return "redirect:/busta";
    }
}
