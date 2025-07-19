package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.StatoPresenza;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

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

    private static final Logger logPresenzaController = Logger.getLogger(PresenzaService.class.getName());


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
                redirectAttributes.addFlashAttribute("presenzaSuccess","Ferie Aggiunte con Successo");
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

        if (presenzaGiornaliera != null &&
                !presenzaGiornaliera.getStato().equals(StatoPresenza.PERMESSO)){
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
        try{
            presenzaService.addPreseza(presenza,principal);
            redirectAttributes.addFlashAttribute("presenzaSuccess","Presenza Salvata con successo");
        }catch (DataFormatException ex){
            redirectAttributes.addFlashAttribute("presenzaError","Orari del permesso inseriti errati");
            return "redirect:/";
        }catch (DateTimeException ex){
            redirectAttributes.addFlashAttribute("presenzaError","Non è possibile inserire presenze nel weekend.");
            return "redirect:/";
        }
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

        String ricercaDataFormat = "";

        // Prima controlla se è null
        if (ricercaData == null || ricercaData.isEmpty() || ricercaData.equals(",")) {
            model.addAttribute("listaPresenze", presenzaRepository.findByListPrenseza(dipendente));
        } else if (ricercaData.startsWith(",") && ricercaData.length() >= 4) {
            ricercaDataFormat = ricercaData.substring(1);
            model.addAttribute("listaPresenze", presenzaRepository.findByMeseLike(dipendente, ricercaDataFormat));
        } else if (ricercaData.endsWith(",")) {
            ricercaDataFormat = ricercaData.substring(0, 10);
            model.addAttribute("listaPresenze", presenzaRepository.findBySearchData(dipendente, ricercaDataFormat));
        } else {
            ricercaDataFormat = ricercaData;
            model.addAttribute("listaPresenze", presenzaRepository.findBySearchData(dipendente, ricercaDataFormat));
        }

        float oreFerieRimanenti = contratto.get().getOreFerieTotali() - contratto.get().getOreFerieUtilizzate();
        model.addAttribute("oreFerieRimanenti", oreFerieRimanenti);

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
                          RedirectAttributes redirectAttributes, Principal principal){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nella modifica dei dati");
            return "redirect:/presenza/calendar/" + formEditDay.getIdPresenza();
        }

        //modifica la presenza
        try {
            presenzaService.editPresenza(formEditDay, principal);
            redirectAttributes.addFlashAttribute("successMessage", "Modifica Effettuata");
            return "redirect:/presenza/calendar/" + formEditDay.getIdPresenza();
        }catch (DataFormatException ex){
            redirectAttributes.addFlashAttribute("errorMessage", "Orari del permesso inseriti errati");
            return "redirect:/presenza/calendar/" + formEditDay.getIdPresenza();
        }
    }

    @GetMapping("/calendar/chiudi-giornata/{idPresenza}")
    public String showChiudiGiornata(@PathVariable("idPresenza") Long idPresenza, Model model){
        Presenza presenzaSelezionata = presenzaRepository.findById(idPresenza).get();
        model.addAttribute("presenza",presenzaSelezionata);
        return "Calendar/Calendar";
    }


    @PostMapping("/calendar/chiudi-giornata/{idPresenza}")
    public String chiudiGiornata(@PathVariable("idPresenza") Long idPresenza, RedirectAttributes redirectAttributes){
        try {
            if (presenzaService.chiudiGiornata(idPresenza)){
                redirectAttributes.addFlashAttribute("successMessage", "Giornata chiusa " +
                        "correttamente");
                return "redirect:/presenza/calendar";
            }else {
                redirectAttributes.addFlashAttribute("successMessage", "Giornata riaperta " +
                        "correttamente");
                return "redirect:/presenza/calendar";
            }
        }catch (ArithmeticException ex){
            redirectAttributes.addFlashAttribute("errorMessage", "Giornata non chiudibile, non svolte" +
                    " 8 ore");
            return "redirect:/presenza/calendar";
        }
    }

    @PostMapping("/add-presenza/file")
    public String addPresenzaFile(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes, Principal
                                  principal){
        if (file.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage","File vuoto, non è possibile" +
                    " procedere con l'inserimento");
            logPresenzaController.warning("Il file inserito è vuoto!");
            return "redirect:/presenza/calendar";
        }

        try {
            presenzaService.inserisciFilePresenze(file,principal);
            redirectAttributes.addFlashAttribute("successMessage","Presenze inserite correttamente");
            logPresenzaController.info("File inserito correttamente!");
            return "redirect:/presenza/calendar";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage","Qualcosa andato storto durante" +
                    " l'inserimento del file");
            return "redirect:/presenza/calendar";
        }
    }

    @GetMapping("/info-file")
    public String showInfoFile(){
        return "Calendar/infoFile";
    }
}
