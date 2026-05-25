package com.gaia.repository;

import com.gaia.entity.EstadoSolicitud;
import com.gaia.entity.Mascota;
import com.gaia.entity.SolicitudAdopcion;
import com.gaia.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> {

    @EntityGraph(attributePaths = {"usuario", "mascota", "mascota.refugio"})
    List<SolicitudAdopcion> findByUsuarioOrderByFechaSolicitudDesc(User usuario);

    @EntityGraph(attributePaths = {"usuario", "mascota", "mascota.refugio"})
    List<SolicitudAdopcion> findAllByOrderByFechaSolicitudDesc();

    List<SolicitudAdopcion> findByMascotaAndEstado(Mascota mascota, EstadoSolicitud estado);

    boolean existsByUsuarioAndMascotaAndEstado(User usuario, Mascota mascota, EstadoSolicitud estado);

    boolean existsByMascotaAndEstado(Mascota mascota, EstadoSolicitud estado);

    long countByEstado(EstadoSolicitud estado);

    void deleteByMascota(Mascota mascota);

    void deleteByUsuario(User usuario);
}
