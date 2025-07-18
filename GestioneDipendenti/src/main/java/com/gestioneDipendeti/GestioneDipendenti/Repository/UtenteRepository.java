package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByUsername (String username);

    Optional<Utente> findByEmail (String email);

    Utente findTopByOrderByIdUtenteDesc();

    List<Utente> findByRole_IdRole(Long idRole);

    @Query("select u FROM Utente u JOIN u.role r WHERE u.idUtente = :idUtente AND r.idRole = :idRole")
    Optional<Utente> findByIdAndRoleId(@Param("idUtente") Long idUtente, @Param("idRole") Long idRole);

    @Query("""
    SELECT u FROM Utente u
    JOIN u.role r
    WHERE u.idUtente = :idUtente
      AND r.idRole = 2
      AND NOT EXISTS (
          SELECT r2 FROM Utente u2 JOIN u2.role r2
          WHERE u2 = u AND r2.idRole <> 2
      )
    """)
    Optional<Utente> findUtenteConSoloRuolo2(@Param("idUtente") Long idUtente);
}
