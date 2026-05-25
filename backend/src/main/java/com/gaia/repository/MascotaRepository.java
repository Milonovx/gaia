package com.gaia.repository;

import com.gaia.entity.Mascota;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MascotaRepository extends JpaRepository<Mascota, Long>, JpaSpecificationExecutor<Mascota> {

    @Override
    @EntityGraph(attributePaths = "refugio")
    Optional<Mascota> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "refugio")
    List<Mascota> findAll(Specification<Mascota> specification);

    long countByDisponibleTrue();
}
