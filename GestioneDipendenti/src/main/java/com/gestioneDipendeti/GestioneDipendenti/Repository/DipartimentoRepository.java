package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipartimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DipartimentoRepository extends JpaRepository<Dipartimento, Long> {

    Dipartimento findByDipartimento(String dipartimento);
}
