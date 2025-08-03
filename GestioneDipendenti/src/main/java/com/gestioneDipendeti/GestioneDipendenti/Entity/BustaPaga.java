package com.gestioneDipendeti.GestioneDipendenti.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class BustaPaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idBustaPaga;

    private LocalDate mese;

    @Column(name = "stipendio_lordo")
    private Float stipendioLordo;

    @Column(name = "numero_straordinari_per_mese")
    private int numeroStraordinariPerMese;

    private float trattenuta;

    @Column(name = "stipendio_netto")
    private float stipendioNetto;

    @ManyToOne
    @JoinColumn(name = "id_dipendente")
    private Dipendente dipendente;

    public Float getStipendioLordo() {
        return stipendioLordo;
    }

    public void setStipendioLordo(Float stipendioLordo) {
        this.stipendioLordo = stipendioLordo;
    }

    public int getNumeroStraordinariPerMese() {
        return numeroStraordinariPerMese;
    }

    public void setNumeroStraordinariPerMese(int numeroStraordinariPerMese) {
        this.numeroStraordinariPerMese = numeroStraordinariPerMese;
    }

    public float getTrattenuta() {
        return trattenuta;
    }

    public void setTrattenuta(float trattenuta) {
        this.trattenuta = trattenuta;
    }

    public float getStipendioNetto() {
        return stipendioNetto;
    }

    public void setStipendioNetto(float stipendioNetto) {
        this.stipendioNetto = stipendioNetto;
    }

    public long getIdBustaPaga() {
        return idBustaPaga;
    }

    public void setIdBustaPaga(long idBustaPaga) {
        this.idBustaPaga = idBustaPaga;
    }

    public LocalDate getMese() {
        return mese;
    }

    public void setMese(LocalDate mese) {
        this.mese = mese;
    }

    public Dipendente getDipendente() {
        return dipendente;
    }

    public void setDipendente(Dipendente dipendente) {
        this.dipendente = dipendente;
    }
}
