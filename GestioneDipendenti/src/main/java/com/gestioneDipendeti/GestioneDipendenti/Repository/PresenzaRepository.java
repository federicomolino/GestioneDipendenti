package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipendente;
import com.gestioneDipendeti.GestioneDipendenti.Entity.Presenza;
import com.gestioneDipendeti.GestioneDipendenti.Entity.StatoPresenza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PresenzaRepository extends JpaRepository<Presenza, Long> {

    @Query("SELECT p FROM Presenza p WHERE p.data = CURRENT_DATE AND p.dipendente = :dipendente")
    List<Presenza> findByPresenza(@Param("dipendente")Dipendente dipendente);

    @Query(value = "SELECT p FROM Presenza p WHERE p.dipendente = :dipendente " +
            "and DATE_FORMAT(p.`data`, '%Y-%m') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m')" +
            "order by data desc")
    List<Presenza> findByListPrenseza(@Param("dipendente") Dipendente dipendente);

    //Ricerca per anno e mese
    @Query(value = "select p from Presenza p where p.dipendente = :dipendente " +
                    "and function('DATE_FORMAT', p.data, '%Y-%m') = :mese")
    List<Presenza> findByMeseLike(@Param("dipendente") Dipendente dipendente, @Param("mese") String mese);

    //Ricerca giorno mese e anno
    @Query("select p from Presenza p where function('DATE_FORMAT', p.data, '%Y-%m-%d') = :data " +
            "and p.dipendente = :dipendente order by p.data DESC")
    List<Presenza> findBySearchData(@Param("dipendente") Dipendente dipendente, @Param("data") String data);

    List<Presenza> findByDataAndDipendente(LocalDate data, Dipendente dipendente);

    @Query("select p from Presenza p where p.data = :data")
    Optional<Presenza> findByData(@Param("data") LocalDate data);

    @Query("select p from Presenza p where p.data = :data and p.stato = :stato and p.dipendente = :dipendente")
    Optional<Presenza> findByDataAndStato(@Param("data") LocalDate data,
                                  @Param("stato") StatoPresenza stato,
                                  @Param("dipendente") Dipendente dipendente);

    @Query("select p from Presenza p where p.data = :data")
    List<Presenza> findByDataList(@Param("data") LocalDate data);

    /*Quello che fa
    SELECT p.* FROM db_gestionedipendenti.presenza p
    WHERE p.id_dipendente = 1
    order by p.id_presenza desc;*/
    Presenza findTopByDipendenteOrderByDataDesc(Dipendente dipendente);
}
