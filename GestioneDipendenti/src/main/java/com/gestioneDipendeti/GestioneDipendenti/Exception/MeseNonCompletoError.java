package com.gestioneDipendeti.GestioneDipendenti.Exception;

public class MeseNonCompletoError extends RuntimeException {
    public MeseNonCompletoError(String message) {
        super("Il mese non è completo di timbrature");
    }
}
