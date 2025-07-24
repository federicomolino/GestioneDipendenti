package com.gestioneDipendeti.GestioneDipendenti.Entity;

import jakarta.persistence.*;

@Entity
public class MacchinaAziendale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idAuto;

    @Column(name = "nome_auto")
    private String nomeAuto;

    @Column(name = "anno_macchina")
    private int anno;

    @Column(name = "numero_kilometri")
    private int numeroKm;

    @OneToOne
    @JoinColumn(name = "id_dipendente")
    private Dipendente dipendente;

    @Column(name = "auto_disponibile", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean autoDisponibile;

    public boolean isAutoDisponibile() {
        return autoDisponibile;
    }

    public void setAutoDisponibile(boolean autoDisponibile) {
        this.autoDisponibile = autoDisponibile;
    }

    public long getIdAuto() {
        return idAuto;
    }

    public void setIdAuto(long idAuto) {
        this.idAuto = idAuto;
    }

    public String getNomeAuto() {
        return nomeAuto;
    }

    public void setNomeAuto(String nomeAuto) {
        this.nomeAuto = nomeAuto;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public int getNumeroKm() {
        return numeroKm;
    }

    public void setNumeroKm(int numeroKm) {
        this.numeroKm = numeroKm;
    }

    public Dipendente getDipendente() {
        return dipendente;
    }

    public void setDipendente(Dipendente dipendente) {
        this.dipendente = dipendente;
    }
}
