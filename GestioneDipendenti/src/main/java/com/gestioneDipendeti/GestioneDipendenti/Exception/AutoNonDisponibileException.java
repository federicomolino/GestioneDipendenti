package com.gestioneDipendeti.GestioneDipendenti.Exception;

public class AutoNonDisponibileException extends RuntimeException {
    public AutoNonDisponibileException(String message) {
        super("L'auto non è disponibile");
    }
}
