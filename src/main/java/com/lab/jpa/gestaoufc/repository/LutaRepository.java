package com.lab.jpa.gestaoufc.repository;

import com.lab.jpa.gestaoufc.domain.model.Luta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LutaRepository extends JpaRepository<Luta, UUID> {

    boolean existsByLutador1IdOrLutador2Id(UUID lutador1, UUID lutador2);
}
