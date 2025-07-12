package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByUsername (String username);

    Optional<Utente> findByEmail (String email);

    Utente findTopByOrderByIdUtenteDesc();

    List<Utente> findByRole_IdRole(Long idRole);
}
