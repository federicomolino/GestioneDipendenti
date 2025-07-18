package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Assistenza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssistenzaRepository extends JpaRepository<Assistenza,Long> {
    List<Assistenza> findByUtente_IdUtenteOrderByIdAssistenzaDesc(@Param("idUtente") long idUtente);

    @Query("select count(a) from Assistenza a where a.utente.idUtente = :idUtente AND a.richiestaChiusa = false")
    Long countRichiesteApertePerUtente(@Param("idUtente") Long idUtente);

    List<Assistenza> findByIdUtenteAperturaOrderByIdAssistenzaDesc(long idUtente);

}
