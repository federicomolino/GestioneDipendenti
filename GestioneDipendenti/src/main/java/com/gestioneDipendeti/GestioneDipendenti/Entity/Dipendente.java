package com.gestioneDipendeti.GestioneDipendenti.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Dipendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dipendente")
    private Long idDipendente;

    private String nome;

    private String cognome;

    @Column(name = "data_di_nascita")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDiNascita;

    @Column(name = "luogo_di_nascita")
    private String luogoDiNascita;

    @OneToMany(mappedBy = "dipendente")
    @JsonIgnore
    List<Contratto> contratto;

    @OneToMany(mappedBy = "dipendente")
    @JsonIgnore
    List<Presenza> presenza;

    @OneToMany(mappedBy = "dipendente")
    @JsonIgnore
    List<Ruolo> ruolo;

    @OneToOne
    @JoinColumn(name = "idUtente")
    @JsonManagedReference
    private Utente utente;

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public List<Ruolo> getRuolo() {
        return ruolo;
    }

    public void setRuolo(List<Ruolo> ruolo) {
        this.ruolo = ruolo;
    }

    public List<Presenza> getPresenza() {
        return presenza;
    }

    public void setPresenza(List<Presenza> presenza) {
        this.presenza = presenza;
    }

    public List<Contratto> getContratto() {
        return contratto;
    }

    public void setContratto(List<Contratto> contratto) {
        this.contratto = contratto;
    }

    public Long getIdDipendente() {
        return idDipendente;
    }

    public void setIdDipendente(Long idDipendente) {
        this.idDipendente = idDipendente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDate getDataDiNascita() {
        return dataDiNascita;
    }

    public void setDataDiNascita(LocalDate dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }

    public String getLuogoDiNascita() {
        return luogoDiNascita;
    }

    public void setLuogoDiNascita(String luogoDiNascita) {
        this.luogoDiNascita = luogoDiNascita;
    }
}
