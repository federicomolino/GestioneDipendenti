package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.MacchinaAziendale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoRepository extends JpaRepository<MacchinaAziendale,Long> {
}
