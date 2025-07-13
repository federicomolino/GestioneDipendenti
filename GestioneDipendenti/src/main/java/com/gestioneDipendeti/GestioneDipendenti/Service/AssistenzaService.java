package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Assistenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.TipologiaRichiestaAssistenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.AssistenzaRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AssistenzaService {

    @Autowired
    private AssistenzaRepository assistenzaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    private static final Logger logAssistenza= Logger.getLogger(AssistenzaService.class.getName());

    public Assistenza addAsstenza(String richiesta,
                                  TipologiaRichiestaAssistenza tipologiaRichiestaAssistenza, long idUdente){
        Utente utenteAdmin = utenteRepository.findById(idUdente).get();
        //Verifico se l'id passato ha i permessi di Admin
        Optional<Utente> utente = utenteRepository.findByIdAndRoleId(utenteAdmin.getIdUtente(), 1L);
        if (!utente.isPresent()){
            logAssistenza.info("l'utente con id:" + idUdente + " non è Admin");
            throw new IllegalArgumentException("l'utente con id:" + idUdente + " non è Admin");
        }
        Assistenza assistenzaAperta = new Assistenza();
        assistenzaAperta.setRichiesta(richiesta);
        assistenzaAperta.setTipologiaRichiestaAssistenza(tipologiaRichiestaAssistenza);
        assistenzaAperta.setRichiestaChiusa(false);
        assistenzaAperta.setUtente(utenteAdmin);
        logAssistenza.info("Richiesta Creata");
        return assistenzaRepository.save(assistenzaAperta);
    }

    public void invioEmailAssistenzaAperta(long idUtente,String richiesta)throws AuthenticationException{
        try{
            Utente utenteAdmin = utenteRepository.findById(idUtente).get();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(utenteAdmin.getEmail());
            message.setSubject("Richiesta Assistenza");
            message.setText("Ciao, " + utenteAdmin.getUsername() +" la richiesta è la seguente:\n" +
                    richiesta);
            javaMailSender.send(message);
            logAssistenza.info("Mail inviata correttamente a " + utenteAdmin.getEmail());
        }catch (Exception e){
            logAssistenza.warning(e.getMessage());
            throw new AuthenticationException("Errore invio email");
        }
    }
}
