package com.gestioneDipendeti.GestioneDipendenti.Entity;

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
    List<Contratto> contratto;

    @OneToMany(mappedBy = "dipendente")
    List<Presenza> presenza;

    @OneToMany(mappedBy = "dipendente")
    List<Ruolo> ruolo;

    @ManyToMany
    @JoinTable(
            name = "dipendente_dipartimento",
            joinColumns = @JoinColumn(name = "idDipendente"),
            inverseJoinColumns = @JoinColumn(name = "idDipartimento")
    )
    private List<Dipartimento> dipartimento;

    @OneToOne
    @JoinColumn(name = "idUtente")
    private Utente utente;

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public List<Dipartimento> getDipartimento() {
        return dipartimento;
    }

    public void setDipartimento(List<Dipartimento> dipartimento) {
        this.dipartimento = dipartimento;
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
