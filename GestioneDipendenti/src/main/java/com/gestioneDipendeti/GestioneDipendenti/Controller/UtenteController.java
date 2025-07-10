package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.DTO.NuovoUtenteDTO;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Role;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RoleRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.LoginService;
import com.gestioneDipendeti.GestioneDipendenti.Service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/utente")
public class

UtenteController {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private LoginService loginService;

    @GetMapping("/nuovoUtente")
    public String showNuovoUtente(Model model){
        List<Utente> utenti = utenteRepository.findAll();
        model.addAttribute("formNewUtente", new NuovoUtenteDTO());
        model.addAttribute("listUtenti",utenti);
        return "Utente/newUtente";
    }

    @PostMapping("/nuovoUtente")
    public String addNuovoUtente(@Valid @ModelAttribute("formNewUtente") NuovoUtenteDTO nuovoUtenteDTO, BindingResult bindingResult,
                                 @RequestParam(name = "ruolo", required = false)List<String> ruoli, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            return "Utente/newUtente";
        }

        LocalDate dataOdierna = LocalDate.now();
        Integer anniDipendente = dataOdierna.getYear() - nuovoUtenteDTO.getDataDiNascita().getYear();
        if (anniDipendente < 18){
            bindingResult.rejectValue("dataDiNascita","erroreData","Data non valida, " +
                    "l'utente non può essere minorenne");
        }


        //Verifica Username
        if (utenteService.usernameExist(nuovoUtenteDTO.getUsername())){
            bindingResult.rejectValue("erroreUsername","erroreUsername","Username già presente nel" +
                    "sistema");
        }

        //Verifica email
        if (utenteService.emailExist(nuovoUtenteDTO.getEmail())){
            bindingResult.rejectValue("email","erroreEmail","Email già presente nel" +
                    "sistema");
        }

        //Salvo utente e dipendente
        utenteRepository.save(utenteService.addUtente(nuovoUtenteDTO,ruoli));
        dipendenteRepository.save(utenteService.addDipendente(nuovoUtenteDTO));
        try{
            utenteService.invioEmailCreazioneUtente(nuovoUtenteDTO.getEmail());
            redirectAttributes.addFlashAttribute("successMessage","Utente creato correttamente");
        }catch (AuthenticationException ex){
            redirectAttributes.addFlashAttribute("warningMessage","Utente creato correttamente," +
                    " mail non inviata!");
            return "redirect:/utente/nuovoUtente";
        }
        return "redirect:/utente/nuovoUtente";
    }

    @PostMapping("/cancellaUtente/{IdUtente}")
    public String cancellaUtente(@PathVariable("IdUtente")Long idUtente, RedirectAttributes redirectAttributes){
        utenteService.cancellaUtente(idUtente);
        utenteService.cancellaDipendente(idUtente);
        redirectAttributes.addFlashAttribute("successMessage","Utente eliminato correttamente");
        return "redirect:/utente/nuovoUtente";
    }

    @GetMapping("/editUtente")
    public String showEditUtente(Model model, Principal principal){
        String username = principal.getName();
        Utente utente = utenteRepository.findByUsername(username).get();
        Dipendente dipendente = dipendenteRepository.findById(utente.getIdUtente()).get();
        NuovoUtenteDTO nuovoUtenteDTO = new NuovoUtenteDTO();
        nuovoUtenteDTO.setNome(dipendente.getNome());
        nuovoUtenteDTO.setCognome(dipendente.getCognome());
        nuovoUtenteDTO.setDataDiNascita(dipendente.getDataDiNascita());
        nuovoUtenteDTO.setLuogoDiNascita(dipendente.getLuogoDiNascita());
        nuovoUtenteDTO.setEmail(utente.getEmail());
        nuovoUtenteDTO.setUsername(utente.getUsername());
        nuovoUtenteDTO.setPassword("");

        List<String> ruoli = new ArrayList<>();
        for (Role ruolo : utente.getRole()){
            ruoli.add(ruolo.getNomeRole());
        }
        nuovoUtenteDTO.setRuoli(ruoli);

        model.addAttribute("formEditUtente",nuovoUtenteDTO);
        return "Utente/editUtente";
    }

    @PostMapping("/editUtente")
    public String editUtente(@Valid @ModelAttribute("formEditUtente") NuovoUtenteDTO nuovoUtenteDTO, BindingResult bindingResult,
                             Principal principal, @RequestParam(name = "ruoli", required = false)List<String> ruoli,
                             RedirectAttributes redirectAttributes){

        Utente utente = loginService.recuperoUtente(principal);

        Optional<Utente> userMail = utenteRepository.findByEmail(nuovoUtenteDTO.getEmail());
        //Verifico se la mail è già presente
        if (userMail.isPresent()){
            if (!userMail.get().getEmail().equals(utente.getEmail())){
                bindingResult.rejectValue("email","errorEmail","Email già presente " +
                        "nel sistema");
                return "Utente/editUtente";
            }
        }

        Optional<Utente> utenteUsername = utenteRepository.findByUsername(nuovoUtenteDTO.getUsername());
        if (!utenteUsername.get().getUsername().equals(utente.getUsername())){
            bindingResult.rejectValue("username","errorUsername","Username già presente " +
                    "nel sistema");
            return "Utente/editUtente";
        }


        if (bindingResult.hasErrors()){
            return "Utente/editUtente";
        }

        //modifico dipendente e utente
        utenteService.editDipendente(nuovoUtenteDTO, principal);
        utenteService.editUtente(nuovoUtenteDTO,principal,ruoli);
        redirectAttributes.addFlashAttribute("successMessage", "Utente modifica correttamente");
        return "redirect:/utente/editUtente";
    }
}
