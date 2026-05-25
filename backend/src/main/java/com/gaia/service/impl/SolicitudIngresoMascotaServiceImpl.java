package com.gaia.service.impl;

import com.gaia.dto.ingreso.ActualizarEstadoIngresoRequest;
import com.gaia.dto.ingreso.SolicitudIngresoMascotaRequest;
import com.gaia.dto.ingreso.SolicitudIngresoMascotaResponse;
import com.gaia.entity.EstadoSolicitud;
import com.gaia.entity.Mascota;
import com.gaia.entity.Refugio;
import com.gaia.entity.Role;
import com.gaia.entity.SolicitudIngresoMascota;
import com.gaia.entity.User;
import com.gaia.service.FileStorageService;
import com.gaia.exception.ApiException;
import com.gaia.exception.ResourceNotFoundException;
import com.gaia.mapper.SolicitudIngresoMascotaMapper;
import com.gaia.repository.MascotaRepository;
import com.gaia.repository.RefugioRepository;
import com.gaia.repository.SolicitudIngresoMascotaRepository;
import com.gaia.repository.UserRepository;
import com.gaia.service.SolicitudIngresoMascotaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudIngresoMascotaServiceImpl implements SolicitudIngresoMascotaService {

    private final SolicitudIngresoMascotaRepository solicitudRepository;
    private final UserRepository userRepository;
    private final RefugioRepository refugioRepository;
    private final MascotaRepository mascotaRepository;
    private final SolicitudIngresoMascotaMapper mapper;
    private final FileStorageService fileStorageService;

    public SolicitudIngresoMascotaServiceImpl(
            SolicitudIngresoMascotaRepository solicitudRepository,
            UserRepository userRepository,
            RefugioRepository refugioRepository,
            MascotaRepository mascotaRepository,
            SolicitudIngresoMascotaMapper mapper,
            FileStorageService fileStorageService
    ) {
        this.solicitudRepository = solicitudRepository;
        this.userRepository = userRepository;
        this.refugioRepository = refugioRepository;
        this.mascotaRepository = mascotaRepository;
        this.mapper = mapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public SolicitudIngresoMascotaResponse create(SolicitudIngresoMascotaRequest request, String email) {
        User usuario = findUser(email);
        if (usuario.getRole() == Role.ROLE_ADMIN) {
            throw new ApiException("Los administradores no pueden entregar mascotas", HttpStatus.FORBIDDEN);
        }
        Refugio refugio = findRefugio(request.getRefugioId());
        String imagenUrl = fileStorageService.storeImage(request.getImagen()).url();
        SolicitudIngresoMascota solicitud = mapper.toEntity(request, usuario, refugio, imagenUrl);
        return mapper.toResponse(solicitudRepository.save(solicitud));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudIngresoMascotaResponse> findAll(String email) {
        User usuario = findUser(email);
        List<SolicitudIngresoMascota> solicitudes = usuario.getRole() == Role.ROLE_ADMIN
                ? solicitudRepository.findAllByOrderByFechaSolicitudDesc()
                : solicitudRepository.findByUsuarioOrderByFechaSolicitudDesc(usuario);
        return solicitudes.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SolicitudIngresoMascotaResponse updateEstado(Long id, ActualizarEstadoIngresoRequest request) {
        SolicitudIngresoMascota solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de ingreso no encontrada con id " + id));
        EstadoSolicitud estadoAnterior = solicitud.getEstadoSolicitud();
        if (estadoAnterior == EstadoSolicitud.APROBADA && request.estadoSolicitud() != EstadoSolicitud.APROBADA) {
            throw new ApiException("No se puede revertir una solicitud aprobada porque ya creó una mascota", HttpStatus.CONFLICT);
        }
        if (estadoAnterior == EstadoSolicitud.APROBADA && request.estadoSolicitud() == EstadoSolicitud.APROBADA) {
            return mapper.toResponse(solicitud);
        }
        solicitud.setEstadoSolicitud(request.estadoSolicitud());
        if (estadoAnterior != EstadoSolicitud.APROBADA && request.estadoSolicitud() == EstadoSolicitud.APROBADA) {
            createMascotaFromSolicitud(solicitud, request.refugioId());
        }
        return mapper.toResponse(solicitudRepository.save(solicitud));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SolicitudIngresoMascota solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de ingreso no encontrada con id " + id));
        solicitudRepository.delete(solicitud);
    }

    private void createMascotaFromSolicitud(SolicitudIngresoMascota solicitud, Long refugioId) {
        Refugio refugio = refugioId != null ? findRefugio(refugioId) : solicitud.getRefugio();

        Mascota mascota = Mascota.builder()
                .nombre(solicitud.getNombreMascota())
                .edad(solicitud.getEdad())
                .tipoEdad(solicitud.getTipoEdad())
                .raza(solicitud.getRaza())
                .tamano(solicitud.getTamano())
                .genero(solicitud.getGenero())
                .descripcion(solicitud.getDescripcion())
                .estadoSalud(solicitud.getEstadoSalud())
                .vacunado(solicitud.getVacunado())
                .esterilizado(solicitud.getEsterilizado())
                .imagenUrl(solicitud.getImagenUrl())
                .disponible(true)
                .refugio(refugio)
                .build();
        mascotaRepository.save(mascota);
    }

    private Refugio findRefugio(Long id) {
        return refugioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refugio no encontrado con id " + id));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
