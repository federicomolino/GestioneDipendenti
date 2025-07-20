package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContrattoRepository extends JpaRepository<Contratto,Long> {

    Optional<Contratto> findBydipendente (Dipendente dipendente);

    @Query("""
        SELECT u.idUtente
        FROM Contratto c
        JOIN c.dipendente d
        JOIN d.utente u
        WHERE c.isScaduto = true
    """)
    List<Long> findIdUtenteWithContrattoScaduto();
}
