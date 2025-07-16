package com.gestioneDipendeti.GestioneDipendenti.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    //La chiave segreta utilizzando HS256 dovrà essere di 32byte (32 caratteri)
    private final String secretKey="questaLaChiaveSegretaPerRichiestaToken";

    public String generaToken(String username){
        return Jwts.builder()
                .setSubject(username) //L'utente che viene codificato nel token
                .setIssuedAt(new Date()) //Data di emissione token
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) //Validità del toje (1h)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // firma token
                .compact(); //Restituiamo come stringa
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
            return null;
        }
    }
}
