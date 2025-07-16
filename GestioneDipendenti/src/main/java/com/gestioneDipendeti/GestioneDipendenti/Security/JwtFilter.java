package com.gestioneDipendeti.GestioneDipendenti.Security;

import com.gestioneDipendeti.GestioneDipendenti.Repository.UtenteRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private UtenteRepository utenteRepository;

    public JwtFilter(TokenService tokenService, UtenteRepository utenteRepository){
        this.tokenService = tokenService;
        this.utenteRepository = utenteRepository;
    }

    //Escludiamo tutte le richieste che iniziano per /api/v1
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        return !request.getRequestURI().startsWith("/api");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        //Viene l'header HTTP Authorization dalla richiesta.
        String authHeader = request.getHeader("Authorization");

        //Escluso quello della verifica token
        if (request.getRequestURI().equals("/api/v1/token")) {
            // Non richiede autenticazione
            filterChain.doFilter(request, response);
            return;
        }


        //Se l'header Authorization manca o non inizia con la stringa "Bearer ",
        // la risposta è 401 Unauthorized con messaggio "Token mancante o non valido".
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token mancante o non valido");
            return;
        }

        String token = authHeader.substring(7);
        String username = tokenService.validationToken(token);

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Token non valido o scaduto");
            return;
        }

        // Imposta l'utente autenticato nel contesto Spring Security
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Se il token è valido, proseguiamo
        filterChain.doFilter(request, response);
    }
}
