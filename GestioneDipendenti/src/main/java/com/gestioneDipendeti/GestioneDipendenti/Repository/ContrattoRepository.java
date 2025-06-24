package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Contratto;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContrattoRepository extends JpaRepository<Contratto,Long> {

    Optional<Contratto> findBydipendente (Dipendente dipendente);
}
