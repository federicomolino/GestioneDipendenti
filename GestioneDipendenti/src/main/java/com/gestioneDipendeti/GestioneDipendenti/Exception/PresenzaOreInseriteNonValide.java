package com.gestioneDipendeti.GestioneDipendenti.Exception;

public class PresenzaOreInseriteNonValide extends RuntimeException {
    public PresenzaOreInseriteNonValide(String message) {
        super("Ore inserite non valide");
    }
}
