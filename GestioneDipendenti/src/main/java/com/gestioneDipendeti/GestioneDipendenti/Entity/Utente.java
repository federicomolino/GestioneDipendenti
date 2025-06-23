package com.gestioneDipendeti.GestioneDipendenti.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IdUtente;

    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utente_role",
            joinColumns = @JoinColumn(name = "idUtente"),
            inverseJoinColumns = @JoinColumn(name = "idRole")
    )
    private List<Role> role;

    @OneToOne(mappedBy = "utente")
    private Dipendente dipendente;

    public Dipendente getDipendente() {
        return dipendente;
    }

    public void setDipendente(Dipendente dipendente) {
        this.dipendente = dipendente;
    }

    public Long getIdUtente() {
        return IdUtente;
    }

    public void setIdUtente(Long idUtente) {
        IdUtente = idUtente;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }
}
