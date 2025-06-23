package com.gestioneDipendeti.GestioneDipendenti.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class NuovoUtenteDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotNull(message = "Data di Nascita obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDiNascita;

    @NotBlank(message = "Luogo di Nascita obbligatorio")
    private String luogoDiNascita;

    @NotBlank(message = "Username richiesto")
    private String username;

    @NotBlank(message = "Devi inserire una password")
    @Size(min = 6, message = "Almeno 6 caratteri")
    private String password;

    @NotBlank(message = "Non può essere vuota")
    @Column(unique = true)
    private String email;

    private List<String> ruoli;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRuoli() {
        return ruoli;
    }

    public void setRuoli(List<String> ruoli) {
        this.ruoli = ruoli;
    }
}
