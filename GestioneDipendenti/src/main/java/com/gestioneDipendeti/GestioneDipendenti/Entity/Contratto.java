package com.gestioneDipendeti.GestioneDipendenti.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Contratto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contratto")
    private Long idContratto;

    @Column(name = "tipologia_contratto")
    @Enumerated(EnumType.STRING)
    private tipologiaContratto tipologiaContratto;

    @Column(nullable = false, name = "data_inizio")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInzio;

    @Column(name = "data_fine")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFine;

    private int oreSettimanali;

    @Column(precision = 10, scale = 2)
    private BigDecimal stipendioLordo;

    @ManyToOne
    @JoinColumn(name = "id_dipendente")
    @JsonBackReference
    private Dipendente dipendente;

    private float oreFerieTotali;

    private float oreFerieUtilizzate;

    public float getOreFerieUtilizzate() {
        return oreFerieUtilizzate;
    }

    public void setOreFerieUtilizzate(float oreFerieUtilizzate) {
        this.oreFerieUtilizzate = oreFerieUtilizzate;
    }

    public float getOreFerieTotali() {
        return oreFerieTotali;
    }

    public void setOreFerieTotali(float oreFerieTotali) {
        this.oreFerieTotali = oreFerieTotali;
    }

    public tipologiaContratto getTipologiaContratto() {
        return tipologiaContratto;
    }

    public void setTipologiaContratto(tipologiaContratto tipologiaContratto) {
        this.tipologiaContratto = tipologiaContratto;
    }


    public Long getIdContratto() {
        return idContratto;
    }

    public void setIdContratto(Long idContratto) {
        this.idContratto = idContratto;
    }

    public LocalDate getDataInzio() {
        return dataInzio;
    }

    public void setDataInzio(LocalDate dataInzio) {
        this.dataInzio = dataInzio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public int getOreSettimanali() {
        return oreSettimanali;
    }

    public void setOreSettimanali(int oreSettimanali) {
        this.oreSettimanali = oreSettimanali;
    }

    public BigDecimal getStipendioLordo() {
        return stipendioLordo;
    }

    public void setStipendioLordo(BigDecimal stipendioLordo) {
        this.stipendioLordo = stipendioLordo;
    }

    public Dipendente getDipendente() {
        return dipendente;
    }

    public void setDipendente(Dipendente dipendente) {
        this.dipendente = dipendente;
    }
}
