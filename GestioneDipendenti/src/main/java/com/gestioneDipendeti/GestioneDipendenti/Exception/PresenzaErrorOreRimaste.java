package com.gestioneDipendeti.GestioneDipendenti.Exception;

public class PresenzaErrorOreRimaste extends RuntimeException {
    public PresenzaErrorOreRimaste(String message) {
        super("Ore rimaste non sufficenti");
    }
}
