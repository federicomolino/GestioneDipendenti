package com.gestioneDipendeti.GestioneDipendenti.Exception;

public class BustaPagaPresente extends RuntimeException {
    public BustaPagaPresente(String message) {
        super("Buste paga già presenti per il mese");
    }
}
