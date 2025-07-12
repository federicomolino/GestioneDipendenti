package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DipendenteRepository extends JpaRepository<Dipendente, Long> {
    @Query("SELECT d FROM Dipendente d WHERE d.utente.id = :idUtente")
    Dipendente findByUtenteId(@Param("idUtente") Long idUtente);
}
