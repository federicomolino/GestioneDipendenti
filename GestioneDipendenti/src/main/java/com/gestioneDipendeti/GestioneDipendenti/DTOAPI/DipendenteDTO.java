package com.gestioneDipendeti.GestioneDipendenti.DTOAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class DipendenteDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotNull(message = "Data di Nascita obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDiNascita;

    @NotBlank(message = "Luogo di Nascita obbligatorio")
    private String luogoDiNascita;

    @JsonProperty("utente")
    private UtenteDTO utenteDTO;

    public UtenteDTO getUtenteDTO() {
        return utenteDTO;
    }

    public void setUtenteDTO(UtenteDTO utenteDTO) {
        this.utenteDTO = utenteDTO;
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
