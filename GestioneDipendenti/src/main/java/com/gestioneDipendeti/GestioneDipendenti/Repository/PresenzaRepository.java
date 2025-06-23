package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PresenzaRepository extends JpaRepository<Presenza, Long> {

    @Query("SELECT p FROM Presenza p WHERE p.data = CURRENT_DATE AND p.dipendente = :dipendente")
    Presenza findByPresenza(@Param("dipendente")Dipendente dipendente);

    @Query(value = "SELECT p FROM Presenza p WHERE p.dipendente = :dipendente " +
            "and DATE_FORMAT(p.`data`, '%Y-%m') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m')" +
            "order by data desc")
    List<Presenza> findByListPrenseza(@Param("dipendente") Dipendente dipendente);

    @Query("select p from Presenza p where function('DATE_FORMAT', p.data, '%Y-%m') = :data " +
            "and p.dipendente = :dipendente order by p.data DESC")
    List<Presenza> findBySearchData(@Param("dipendente") Dipendente dipendente, @Param("data") String data);

    boolean existsByDataAndDipendente(LocalDate data, Dipendente dipendente);
}
