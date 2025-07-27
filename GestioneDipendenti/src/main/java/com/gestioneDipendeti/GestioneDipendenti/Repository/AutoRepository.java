package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.MacchinaAziendale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutoRepository extends JpaRepository<MacchinaAziendale,Long> {

    Optional<MacchinaAziendale> findByDipendente_IdDipendente(Long idDipendente);
}
