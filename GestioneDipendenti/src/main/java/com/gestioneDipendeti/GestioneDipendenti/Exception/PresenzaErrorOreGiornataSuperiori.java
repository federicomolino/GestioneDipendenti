package com.gestioneDipendeti.GestioneDipendenti.Exception;

public class PresenzaErrorOreGiornataSuperiori extends RuntimeException {
    public PresenzaErrorOreGiornataSuperiori(String message) {
        super("ore sono superiori ad 8");
    }
}
