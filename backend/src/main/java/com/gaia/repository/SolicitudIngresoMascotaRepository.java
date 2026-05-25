package com.gaia.repository;

import com.gaia.entity.EstadoSolicitud;
import com.gaia.entity.SolicitudIngresoMascota;
import com.gaia.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudIngresoMascotaRepository extends JpaRepository<SolicitudIngresoMascota, Long> {

    @EntityGraph(attributePaths = {"usuario", "refugio"})
    List<SolicitudIngresoMascota> findByUsuarioOrderByFechaSolicitudDesc(User usuario);

    @EntityGraph(attributePaths = {"usuario", "refugio"})
    List<SolicitudIngresoMascota> findAllByOrderByFechaSolicitudDesc();

    long countByEstadoSolicitud(EstadoSolicitud estadoSolicitud);

    void deleteByUsuario(User usuario);
}
