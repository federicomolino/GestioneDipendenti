package com.gestioneDipendeti.GestioneDipendenti.Repository;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByNomeRole(String nome);
}
