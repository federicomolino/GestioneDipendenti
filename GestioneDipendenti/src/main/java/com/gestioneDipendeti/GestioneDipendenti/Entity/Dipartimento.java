package com.gestioneDipendeti.GestioneDipendenti.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class Dipartimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dipartimento")
    private Long idDipartimento;

    @NotBlank(message = "Non può essere vuoto")
    private String dipartimento;

    private int dipendentiPresenti;

    @NotBlank(message = "Non può essere vuoto")
    private String descrizioneDipartimento;

    @ManyToMany(mappedBy = "dipartimento")
    private List<Dipendente> dipendente;

    public Long getIdDipartimento() {
        return idDipartimento;
    }

    public void setIdDipartimento(Long idDipartimento) {
        this.idDipartimento = idDipartimento;
    }

    public String getDipartimento() {
        return dipartimento;
    }

    public void setDipartimento(String dipartimento) {
        this.dipartimento = dipartimento;
    }

    public int getDipendentiPresenti() {
        return dipendentiPresenti;
    }

    public void setDipendentiPresenti(int dipendentiPresenti) {
        this.dipendentiPresenti = dipendentiPresenti;
    }

    public List<Dipendente> getDipendente() {
        return dipendente;
    }

    public void setDipendente(List<Dipendente> dipendente) {
        this.dipendente = dipendente;
    }

    public String getDescrizioneDipartimento() {
        return descrizioneDipartimento;
    }

    public void setDescrizioneDipartimento(String descrizioneDipartimento) {
        this.descrizioneDipartimento = descrizioneDipartimento;
    }
}
