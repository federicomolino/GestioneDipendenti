package com.gestioneDipendeti.GestioneDipendenti.RestAPI;

import com.gestioneDipendeti.GestioneDipendenti.DTO.NuovoUtenteDTO;
import com.gestioneDipendeti.GestioneDipendenti.DTOAPI.DipendenteDTO;
import com.gestioneDipendeti.GestioneDipendenti.DTOAPI.RoleDTO;
import com.gestioneDipendeti.GestioneDipendenti.DTOAPI.UtenteDTO;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RoleRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/utente")
public class ApiUtente {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private RoleRepository roleRepository;

    private static final Logger logApiUtente = Logger.getLogger(ApiUtente.class.getName());

    @GetMapping
    public ResponseEntity<?> showUtenti(){
        List<Utente> utenti = utenteRepository.findAll();
        if (utenti.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(utenti);
    }

    @GetMapping("/dipendenti")
    public List<Dipendente> showDipendente(){
        List<Dipendente> dipendenti = dipendenteRepository.findAll();
        if (dipendenti.isEmpty()){
            ResponseEntity.notFound().build();
            return dipendenti;
        }
        ResponseEntity.ok(dipendenti);
        return dipendenti;
    }

    @PostMapping("/dipendente/new")
    public ResponseEntity<DipendenteDTO> addUtenteDipendente(@RequestBody DipendenteDTO dipendenteDTO){
        UtenteDTO utenteDTO = dipendenteDTO.getUtenteDTO();
        List<RoleDTO> roleDTO = dipendenteDTO.getUtenteDTO().getRoleDTO();
        List<String> ruoliPassati = new ArrayList<>();

        //Verifico anni persona
        int dataNascita = dipendenteDTO.getDataDiNascita().getYear();
        int dataDiOggi = LocalDate.now().getYear();

        if (dataDiOggi - dataNascita < 18){
            logApiUtente.warning("La persona è minorenne");
            return ResponseEntity.badRequest().build();
        }

        if (utenteService.emailExist(utenteDTO.getEmail())){
            logApiUtente.warning("Email già presente nel sistema");
            return ResponseEntity.badRequest().build();
        }

        if (utenteService.usernameExist(utenteDTO.getUsername())){
            logApiUtente.warning("Username già presente nel sistema");
            return ResponseEntity.badRequest().build();
        }
        NuovoUtenteDTO nuovoUtenteDTO = new NuovoUtenteDTO();
        nuovoUtenteDTO.setNome(dipendenteDTO.getNome());
        nuovoUtenteDTO.setCognome(dipendenteDTO.getCognome());
        nuovoUtenteDTO.setDataDiNascita(dipendenteDTO.getDataDiNascita());
        nuovoUtenteDTO.setLuogoDiNascita(dipendenteDTO.getLuogoDiNascita());
        nuovoUtenteDTO.setUsername(utenteDTO.getUsername());
        nuovoUtenteDTO.setPassword(utenteDTO.getPassword());
        nuovoUtenteDTO.setEmail(utenteDTO.getEmail());
        for (RoleDTO roleDto : roleDTO){
            ruoliPassati.add(roleDto.getNomeRole());
        }
        nuovoUtenteDTO.setRuoli(ruoliPassati);
        try {
            utenteRepository.save(utenteService.addUtente(nuovoUtenteDTO,ruoliPassati));
            dipendenteRepository.save(utenteService.addDipendente(nuovoUtenteDTO));
        }catch (Exception ex){
            logApiUtente.warning("Qualcosa è andato storto durante la creazione dell'utente o del dipendente");
            return ResponseEntity.internalServerError().build();
        }
        logApiUtente.info("Utente creato correttamente");
       return ResponseEntity.ok(dipendenteDTO);
    }
}
