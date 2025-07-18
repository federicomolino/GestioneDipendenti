package com.gestioneDipendeti.GestioneDipendenti.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

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

    @ManyToOne
    @JoinColumn(name = "id_utente")
    @JsonIgnore
    private Utente utente;

    @Column(name = "id_utente_apertura")
    private long idUtenteApertura;

    @Column(name = "ora_apertura")
    private LocalDate orarioApertura;

    @Column(name = "risposta_assistenza")
    private String rispostaAssistenza;

    public String getRispostaAssistenza() {
        return rispostaAssistenza;
    }

    public void setRispostaAssistenza(String rispostaAssistenza) {
        this.rispostaAssistenza = rispostaAssistenza;
    }

    public long getIdUtenteApertura() {
        return idUtenteApertura;
    }

    public void setIdUtenteApertura(long idUtenteApertura) {
        this.idUtenteApertura = idUtenteApertura;
    }

    public LocalDate getOrarioApertura() {
        return orarioApertura;
    }

    public void setOrarioApertura(LocalDate orarioApertura) {
        this.orarioApertura = orarioApertura;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

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
