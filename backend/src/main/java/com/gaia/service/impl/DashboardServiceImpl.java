package com.gaia.service.impl;

import com.gaia.dto.dashboard.DashboardStatsResponse;
import com.gaia.entity.EstadoSolicitud;
import com.gaia.repository.MascotaRepository;
import com.gaia.repository.SolicitudAdopcionRepository;
import com.gaia.repository.SolicitudIngresoMascotaRepository;
import com.gaia.repository.UserRepository;
import com.gaia.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final MascotaRepository mascotaRepository;
    private final UserRepository userRepository;
    private final SolicitudAdopcionRepository solicitudRepository;
    private final SolicitudIngresoMascotaRepository ingresoRepository;

    public DashboardServiceImpl(
            MascotaRepository mascotaRepository,
            UserRepository userRepository,
            SolicitudAdopcionRepository solicitudRepository,
            SolicitudIngresoMascotaRepository ingresoRepository
    ) {
        this.mascotaRepository = mascotaRepository;
        this.userRepository = userRepository;
        this.solicitudRepository = solicitudRepository;
        this.ingresoRepository = ingresoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        return new DashboardStatsResponse(
                totalMascotas(),
                totalUsuarios(),
                solicitudesPendientes(),
                mascotasDisponibles(),
                ingresoRepository.countByEstadoSolicitud(EstadoSolicitud.PENDIENTE)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long totalMascotas() {
        return mascotaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long totalUsuarios() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long solicitudesPendientes() {
        return solicitudRepository.countByEstado(EstadoSolicitud.PENDIENTE);
    }

    @Override
    @Transactional(readOnly = true)
    public long mascotasDisponibles() {
        return mascotaRepository.countByDisponibleTrue();
    }
}
