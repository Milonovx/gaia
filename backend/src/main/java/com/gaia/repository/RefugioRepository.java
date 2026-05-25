package com.gaia.repository;

import com.gaia.entity.Refugio;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefugioRepository extends JpaRepository<Refugio, Long> {

    Optional<Refugio> findByNombre(String nombre);
}
