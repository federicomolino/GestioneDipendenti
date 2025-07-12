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
                                  TipologiaRichiestaAssistenza tipologiaRichiestaAssistenza){
        Assistenza assistenzaAperta = new Assistenza();
        assistenzaAperta.setRichiesta(richiesta);
        assistenzaAperta.setTipologiaRichiestaAssistenza(tipologiaRichiestaAssistenza);
        assistenzaAperta.setRichiestaChiusa(false);
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
