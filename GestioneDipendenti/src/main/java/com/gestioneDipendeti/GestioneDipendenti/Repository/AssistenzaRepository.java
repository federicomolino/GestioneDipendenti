package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Assistenza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssistenzaRepository extends JpaRepository<Assistenza,Long> {
    List<Assistenza> findByUtente_IdUtente(@Param("idUtente") long idUtente);
}
