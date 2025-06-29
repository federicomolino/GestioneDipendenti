package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.DTO.NuovoUtenteDTO;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Role;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RoleRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JavaMailSender javaMailSender;

    public Dipendente addDipendente(NuovoUtenteDTO nuovoUtenteDTO){
        Dipendente nuovoDipendente = new Dipendente();
        nuovoDipendente.setNome(nuovoUtenteDTO.getNome());
        nuovoDipendente.setCognome(nuovoUtenteDTO.getCognome());
        nuovoDipendente.setDataDiNascita(nuovoUtenteDTO.getDataDiNascita());
        nuovoDipendente.setLuogoDiNascita(nuovoUtenteDTO.getLuogoDiNascita());
        return nuovoDipendente;
    }

    public Dipendente editDipendente (NuovoUtenteDTO nuovoUtenteDTO, Principal principal){
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = dipendenteRepository.findById(utente.getIdUtente()).get();
        dipendente.setCognome(nuovoUtenteDTO.getCognome());
        dipendente.setDataDiNascita(nuovoUtenteDTO.getDataDiNascita());
        dipendente.setLuogoDiNascita(nuovoUtenteDTO.getLuogoDiNascita());
        dipendente.setNome(nuovoUtenteDTO.getNome());
        return dipendenteRepository.save(dipendente);
    }

    public Utente addUtente (NuovoUtenteDTO nuovoUtenteDTO, List<String> ruoli){
        Utente nuovoUtente = new Utente();
        nuovoUtente.setUsername(nuovoUtenteDTO.getUsername());
        nuovoUtente.setPassword("{noop}" + nuovoUtenteDTO.getPassword());
        nuovoUtente.setEmail(nuovoUtenteDTO.getEmail());

        //Converto stringa nell'oggetto ruolo
        List<Role> listaRuoli = new ArrayList<>();
        for (String nome : ruoli){
            Role ruolo = roleRepository.findByNomeRole(nome).get();
            listaRuoli.add(ruolo);
        }
        nuovoUtente.setRole(listaRuoli);
        return nuovoUtente;
    }

    public Utente editUtente(NuovoUtenteDTO nuovoUtenteDTO, Principal principal, List<String> ruoli){
        Utente utente = loginService.recuperoUtente(principal);
        Utente utenteDaModificare = utenteRepository.findById(utente.getIdUtente()).get();
        utenteDaModificare.setEmail(nuovoUtenteDTO.getEmail());
        utenteDaModificare.setPassword("{noop}" + nuovoUtenteDTO.getPassword());
        utenteDaModificare.setUsername(nuovoUtenteDTO.getUsername());

        if (ruoli != null && !ruoli.isEmpty()){
            List<Role> listaRuoli = new ArrayList<>();
            for (String nome : ruoli){
                Role ruolo = roleRepository.findByNomeRole(nome).get();
                listaRuoli.add(ruolo);
            }
            utenteDaModificare.setRole(listaRuoli);
            return utenteRepository.save(utenteDaModificare);
        }else {
            return utenteRepository.save(utenteDaModificare);
        }
    }

    public void cancellaUtente(Long idUtente){
        Utente utente = utenteRepository.findById(idUtente).get();
        utenteRepository.delete(utente);
    }

    public void cancellaDipendente(Long idUtente){
        Dipendente dipendente = dipendenteRepository.findById(idUtente).get();
        dipendenteRepository.delete(dipendente);
    }

    //InvioEmail
    public void invioEmailCreazioneUtente(String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Registrazione Confermata!");
        message.setText("Ciao, confermiamo la registrazione");
        javaMailSender.send(message);
    }
}
