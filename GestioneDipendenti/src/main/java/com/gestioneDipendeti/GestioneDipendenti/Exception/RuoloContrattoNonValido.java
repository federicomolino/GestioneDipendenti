package com.gestioneDipendeti.GestioneDipendenti.Exception;

public class RuoloContrattoNonValido extends RuntimeException {
    public RuoloContrattoNonValido(String message) {
        super("Ruolo Contratto Non Valido");
    }
}
