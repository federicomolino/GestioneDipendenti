package com.gestioneDipendeti.GestioneDipendenti.Security;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Role;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUserDetails implements UserDetails {

    private final Long idUtente;
    private final String username;
    private final String password;
    private List<GrantedAuthority> authorities;

    public DatabaseUserDetails(Utente utente){
        this.idUtente = utente.getIdUtente();
        this.username = utente.getUsername();
        this.password = utente.getPassword();

        authorities = new ArrayList<>();
        for (Role role : utente.getRole()){
            authorities.add(new SimpleGrantedAuthority(role.getNomeRole()));
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
