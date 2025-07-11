package com.gestioneDipendeti.GestioneDipendenti.Entity;

import jakarta.persistence.*;

@Entity
public class Assistenza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idAssistenza;

    private String richiesta;

    @Enumerated(EnumType.STRING)
    private TipologiaRichiestaAssistenza tipologiaRichiestaAssistenza;

    @Column(name = "isChiusa")
    private boolean richiestaChiusa;

    public boolean isRichiestaChiusa() {
        return richiestaChiusa;
    }

    public void setRichiestaChiusa(boolean richiestaChiusa) {
        this.richiestaChiusa = richiestaChiusa;
    }

    public long getIdAssistenza() {
        return idAssistenza;
    }

    public void setIdAssistenza(long idAssistenza) {
        this.idAssistenza = idAssistenza;
    }

    public String getRichiesta() {
        return richiesta;
    }

    public void setRichiesta(String richiesta) {
        this.richiesta = richiesta;
    }

    public TipologiaRichiestaAssistenza getTipologiaRichiestaAssistenza() {
        return tipologiaRichiestaAssistenza;
    }

    public void setTipologiaRichiestaAssistenza(TipologiaRichiestaAssistenza tipologiaRichiestaAssistenza) {
        this.tipologiaRichiestaAssistenza = tipologiaRichiestaAssistenza;
    }
}
