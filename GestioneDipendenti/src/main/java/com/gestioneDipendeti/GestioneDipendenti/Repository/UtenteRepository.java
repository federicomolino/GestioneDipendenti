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
}
