package com.gestioneDipendeti.GestioneDipendenti.RestAPI;

import com.gestioneDipendeti.GestioneDipendenti.Service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/token")
public class Token {

    private final TokenService tokenService;

    public Token(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @PostMapping()
    public ResponseEntity<Map<String,String>> getToken(@RequestBody Map<String, String> request){
        try{
            String username = request.get("username");
            String token = tokenService.generaToken(username);
            return ResponseEntity.ok(Map.of("token",token));
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("username:","username non valido"));
        }catch (IllegalAccessError ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("username:", "utente non abilitato"));
        }
    }
}
