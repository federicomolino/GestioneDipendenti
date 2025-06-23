package com.gestioneDipendeti.GestioneDipendenti.Entity;

import jakarta.persistence.*;

@Entity
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruolo")
    private Long idRuolo;

    private String nome;

    private String descrizione;

    private int livello;

    private String areaFunzionale;

    @ManyToOne
    @JoinColumn(name = "id_dipendente")
    private Dipendente dipendente;

    public Long getIdRuolo() {
        return idRuolo;
    }

    public void setIdRuolo(Long idRuolo) {
        this.idRuolo = idRuolo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getLivello() {
        return livello;
    }

    public void setLivello(int livello) {
        this.livello = livello;
    }

    public String getAreaFunzionale() {
        return areaFunzionale;
    }

    public void setAreaFunzionale(String areaFunzionale) {
        this.areaFunzionale = areaFunzionale;
    }

    public Dipendente getDipendente() {
        return dipendente;
    }

    public void setDipendente(Dipendente dipendente) {
        this.dipendente = dipendente;
    }
}
