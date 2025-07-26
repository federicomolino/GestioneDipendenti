package com.gestioneDipendeti.GestioneDipendenti.Exception;

public class AutoPerDipendeteExist extends RuntimeException {
    public AutoPerDipendeteExist(String message) {
        super("Utente ha già l'auto");
    }
}
