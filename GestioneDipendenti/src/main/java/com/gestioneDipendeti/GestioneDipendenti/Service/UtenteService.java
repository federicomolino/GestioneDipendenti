package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.DTO.NuovoUtenteDTO;
import com.gestioneDipendeti.GestioneDipendenti.Entity.*;
import com.gestioneDipendeti.GestioneDipendenti.Repository.ContrattoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipendenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RoleRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private ContrattoRepository contrattoRepository;

    public Dipendente addDipendente(NuovoUtenteDTO nuovoUtenteDTO){
        Utente utenteCreato = utenteRepository.findTopByOrderByIdUtenteDesc();
        Dipendente nuovoDipendente = new Dipendente();
        nuovoDipendente.setNome(nuovoUtenteDTO.getNome());
        nuovoDipendente.setCognome(nuovoUtenteDTO.getCognome());
        nuovoDipendente.setDataDiNascita(nuovoUtenteDTO.getDataDiNascita());
        nuovoDipendente.setLuogoDiNascita(nuovoUtenteDTO.getLuogoDiNascita());
        nuovoDipendente.setUtente(utenteCreato);
        return nuovoDipendente;
    }

    public Dipendente editDipendente (NuovoUtenteDTO nuovoUtenteDTO, Principal principal){
        Utente utente = loginService.recuperoUtente(principal);
        Dipendente dipendente = dipendenteRepository.findByUtenteId(utente.getIdUtente());
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

        //Caso in cui non venga passato nessun ruolo
        if (ruoli == null || ruoli.isEmpty()){
            List<Role> listaRuoliDefault = new ArrayList<>();
            Role ruolo = roleRepository.findByNomeRole("USER").get();
            listaRuoliDefault.add(ruolo);
            nuovoUtente.setRole(listaRuoliDefault);
            return nuovoUtente;
        }
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
        utente.setDipendente(null);
        //Salvo utente senza nessuna referenza di dipendente
        utenteRepository.save(utente);
        cancellaDipendente(idUtente);
        utenteRepository.delete(utente);
    }

    public void cancellaDipendente(Long idUtente){
        Dipendente dipendente = dipendenteRepository.findByUtenteId(idUtente);
        dipendente.setUtente(null);
        //Salvo dipendente senza nessuna referenza di utente
        dipendenteRepository.save(dipendente);
        dipendenteRepository.delete(dipendente);
    }

    //InvioEmail
    public void invioEmailCreazioneUtente(String email) throws AuthenticationException {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Registrazione Confermata!");
            message.setText("Ciao, confermiamo la registrazione");
            javaMailSender.send(message);
        }catch (Exception ex){
            throw new AuthenticationException();
        }
    }

    //Verifico esistenza username nel sistema
    public boolean emailExist(String email){
        Optional<Utente> utenteMail = utenteRepository.findByEmail(email);
        if (utenteMail.isPresent()){
            return true;
        }else {
            return false;
        }
    }

    public boolean usernameExist(String username){
        Optional<Utente> utenteUsername = utenteRepository.findByUsername(username);
        if (utenteUsername.isPresent()){
            return true;
        }else {
            return false;
        }
    }

    public List<Long> contrattiScaduti(List<Utente> utenti){
        //Lista degli utenti con contratto scaduto
        List<Long> utentiConContrattoScaduto = new ArrayList<>();
        for(Utente u : utenti){
            Dipendente dipendente = dipendenteRepository.findByUtenteId(u.getIdUtente());
            Optional<Contratto> contattoPerUtente = contrattoRepository.findBydipendente(dipendente);
            if (contattoPerUtente.isPresent()){
                if (contattoPerUtente.get().getTipologiaContratto().equals(tipologiaContratto.DETERMINATO) ||
                        contattoPerUtente.get().getTipologiaContratto().equals(tipologiaContratto.APPRENDISTATO) ||
                        contattoPerUtente.get().getTipologiaContratto().equals(tipologiaContratto.PARTTIME) ||
                        contattoPerUtente.get().getTipologiaContratto().equals(tipologiaContratto.INTERMITTENTE)){
                    //Aggiungi alla lista gli id dei dipendenti che hanno il contratto scaduto
                    if (contattoPerUtente.get().getDataFine().isBefore(LocalDate.now())){
                        utentiConContrattoScaduto.add(u.getIdUtente());
                        Contratto contrattoDaDisabilitara = contattoPerUtente.get();
                        contrattoDaDisabilitara.setScaduto(true);
                        contrattoRepository.save(contrattoDaDisabilitara);
                    }
                }
            }
        }
        return utentiConContrattoScaduto;
    }
}
