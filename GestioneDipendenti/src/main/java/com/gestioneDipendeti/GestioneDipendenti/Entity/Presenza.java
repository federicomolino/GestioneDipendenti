package com.gestioneDipendeti.GestioneDipendenti.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Presenza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presenza")
    private Long idPresenza;

    private LocalDate data;

    @Column(name = "ora_entrata", columnDefinition = "TIME(0)")
    private LocalTime oraEntrata;

    @Column(name = "ora_uscita", columnDefinition = "TIME(0)")
    private LocalTime oraUscita;

    @NotBlank(message = "non può essere vuota")
    private String modalita;

    private LocalDate dataInizioFerie;

    private LocalDate dataFineFerie;

    @NotNull(message = "Lo stato è obbligatorio")
    @Enumerated(EnumType.STRING)
    private StatoPresenza stato;

    @ManyToOne
    @JoinColumn(name = "id_dipendente")
    @JsonBackReference
    private Dipendente dipendente;

    private boolean chiudiGiornata;

    public boolean isChiudiGiornata() {
        return chiudiGiornata;
    }

    public boolean setChiudiGiornata(boolean chiudiGiornata) {
        this.chiudiGiornata = chiudiGiornata;
        return chiudiGiornata;
    }

    public LocalDate getDataFineFerie() {
        return dataFineFerie;
    }

    public void setDataFineFerie(LocalDate dataFineFerie) {
        this.dataFineFerie = dataFineFerie;
    }

    public LocalDate getDataInizioFerie() {
        return dataInizioFerie;
    }

    public void setDataInizioFerie(LocalDate dataInizioFerie) {
        this.dataInizioFerie = dataInizioFerie;
    }

    public Presenza(){
        this.data = LocalDate.now();
    }

    public String getModalita() {
        return modalita;
    }

    public void setModalita(String modalita) {
        this.modalita = modalita;
    }

    public StatoPresenza getStato() {
        return stato;
    }

    public void setStato(StatoPresenza stato) {
        this.stato = stato;
    }

    public Dipendente getDipendente() {
        return dipendente;
    }

    public void setDipendente(Dipendente dipendente) {
        this.dipendente = dipendente;
    }

    public Long getIdPresenza() {
        return idPresenza;
    }

    public void setIdPresenza(Long idPresenza) {
        this.idPresenza = idPresenza;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOraEntrata() {
        return oraEntrata;
    }

    public void setOraEntrata(LocalTime oraEntrata) {
        this.oraEntrata = oraEntrata;
    }

    public LocalTime getOraUscita() {
        return oraUscita;
    }

    public void setOraUscita(LocalTime oraUscita) {
        this.oraUscita = oraUscita;
    }
}
