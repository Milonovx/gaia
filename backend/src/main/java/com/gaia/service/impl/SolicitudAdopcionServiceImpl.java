package com.gaia.service.impl;

import com.gaia.dto.solicitud.ActualizarEstadoSolicitudRequest;
import com.gaia.dto.solicitud.SolicitudAdopcionRequest;
import com.gaia.dto.solicitud.SolicitudAdopcionResponse;
import com.gaia.entity.EstadoSolicitud;
import com.gaia.entity.Mascota;
import com.gaia.entity.Role;
import com.gaia.entity.SolicitudAdopcion;
import com.gaia.entity.User;
import com.gaia.exception.ApiException;
import com.gaia.exception.ResourceNotFoundException;
import com.gaia.mapper.SolicitudAdopcionMapper;
import com.gaia.repository.MascotaRepository;
import com.gaia.repository.SolicitudAdopcionRepository;
import com.gaia.repository.UserRepository;
import com.gaia.service.SolicitudAdopcionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

    private final SolicitudAdopcionRepository solicitudRepository;
    private final UserRepository userRepository;
    private final MascotaRepository mascotaRepository;
    private final SolicitudAdopcionMapper solicitudMapper;

    public SolicitudAdopcionServiceImpl(
            SolicitudAdopcionRepository solicitudRepository,
            UserRepository userRepository,
            MascotaRepository mascotaRepository,
            SolicitudAdopcionMapper solicitudMapper
    ) {
        this.solicitudRepository = solicitudRepository;
        this.userRepository = userRepository;
        this.mascotaRepository = mascotaRepository;
        this.solicitudMapper = solicitudMapper;
    }

    @Override
    @Transactional
    public SolicitudAdopcionResponse create(SolicitudAdopcionRequest request, String email) {
        User usuario = findUserByEmail(email);
        if (usuario.getRole() == Role.ROLE_ADMIN) {
            throw new ApiException("Los administradores no pueden solicitar adopciones", HttpStatus.FORBIDDEN);
        }
        Mascota mascota = mascotaRepository.findById(request.mascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con id " + request.mascotaId()));

        if (!Boolean.TRUE.equals(mascota.getDisponible())) {
            throw new ApiException("La mascota no está disponible para adopción", HttpStatus.CONFLICT);
        }
        if (solicitudRepository.existsByUsuarioAndMascotaAndEstado(usuario, mascota, EstadoSolicitud.PENDIENTE)) {
            throw new ApiException("Ya tienes una solicitud pendiente para esta mascota", HttpStatus.CONFLICT);
        }

        SolicitudAdopcion solicitud = SolicitudAdopcion.builder()
                .usuario(usuario)
                .mascota(mascota)
                .mensaje(request.mensaje())
                .estado(EstadoSolicitud.PENDIENTE)
                .build();

        return solicitudMapper.toResponse(solicitudRepository.save(solicitud));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SolicitudAdopcion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con id " + id));
        solicitudRepository.delete(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAdopcionResponse> findAll(String email) {
        User usuario = findUserByEmail(email);
        List<SolicitudAdopcion> solicitudes = usuario.getRole() == Role.ROLE_ADMIN
                ? solicitudRepository.findAllByOrderByFechaSolicitudDesc()
                : solicitudRepository.findByUsuarioOrderByFechaSolicitudDesc(usuario);

        return solicitudes.stream()
                .map(solicitudMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SolicitudAdopcionResponse updateEstado(Long id, ActualizarEstadoSolicitudRequest request) {
        SolicitudAdopcion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con id " + id));
        solicitud.setEstado(request.estado());
        if (request.estado() == EstadoSolicitud.APROBADA) {
            solicitud.getMascota().setDisponible(false);
            rejectOtherPendingRequests(solicitud);
        } else {
            syncMascotaAvailability(solicitud.getMascota());
        }
        return solicitudMapper.toResponse(solicitudRepository.save(solicitud));
    }

    private void rejectOtherPendingRequests(SolicitudAdopcion approvedSolicitud) {
        solicitudRepository.findByMascotaAndEstado(approvedSolicitud.getMascota(), EstadoSolicitud.PENDIENTE).stream()
                .filter(solicitud -> !solicitud.getId().equals(approvedSolicitud.getId()))
                .forEach(solicitud -> solicitud.setEstado(EstadoSolicitud.RECHAZADA));
    }

    private void syncMascotaAvailability(Mascota mascota) {
        boolean hasApprovedRequest = solicitudRepository.existsByMascotaAndEstado(mascota, EstadoSolicitud.APROBADA);
        mascota.setDisponible(!hasApprovedRequest);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
