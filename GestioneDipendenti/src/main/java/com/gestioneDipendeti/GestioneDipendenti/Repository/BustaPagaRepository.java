package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.BustaPaga;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BustaPagaRepository extends JpaRepository<BustaPaga, Long> {
    List<BustaPaga> findByDipendente(Dipendente dipendente);

    @Query(value = "select b from BustaPaga b where function('DATE_FORMAT', b.mese, '%Y-%m') = :mese")
    List<BustaPaga> findByMeseLike(@Param("mese") String mese);

    //Ricerca per anno e mese
    @Query(value = "select b from BustaPaga b where b.dipendente = :dipendente " +
            "and function('DATE_FORMAT', b.mese, '%Y-%m') = :mese")
    Optional<BustaPaga> findByMeseLikeAndDipendente(@Param("dipendente") Dipendente dipendente, @Param("mese") String mese);
}
