package com.gestioneDipendeti.GestioneDipendenti.Service;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TokenService {

    private final UtenteRepository utenteRepository;

    private final Logger logTokenService = Logger.getLogger(TokenService.class.getName());

    public TokenService(UtenteRepository utenteRepository){
        this.utenteRepository = utenteRepository;
    }

    //La chiave segreta utilizzando HS256 dovrà essere di 32byte (32 caratteri)
    private final String secretKey="questaLaChiaveSegretaPerRichiestaToken";

    public String generaToken(String username) throws UsernameNotFoundException, IllegalAccessError{
        //Verifco utente passato e se ha il ruolo adatto "SERVICE"
        Optional<Utente> utente = utenteRepository.findByUsername(username);
        if (utente.isEmpty()){
            logTokenService.warning("Utente non presente a db");
            throw new UsernameNotFoundException("Utente non presente a db");
        }
        if (utenteRepository.findByIdAndRoleId(utente.get().getIdUtente(),3L).isEmpty()){
            logTokenService.warning("Regole utente non valide");
            throw new IllegalAccessError("Regole utente non valide");
        }
        //Token generato è composto da base64()
        String token = Jwts.builder()
                .setSubject(username) //L'utente che viene codificato nel token
                .setIssuedAt(new Date()) //Data di emissione token
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) //Validità del toje (1h)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // firma token
                .compact(); //Restituiamo come stringa
        logTokenService.info(username + " : " + token);
        return token;
    }

    public String validationToken(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)//Decodifichiamo il token
                    .getBody()
                    .getSubject();//Ritorniamo l'utente
        }catch (JwtException e){
            logTokenService.info("token non valido");
            return null;
        }
    }
}
