package com.gaia.mapper;

import com.gaia.dto.ingreso.SolicitudIngresoMascotaRequest;
import com.gaia.dto.ingreso.SolicitudIngresoMascotaResponse;
import com.gaia.entity.EstadoSolicitud;
import com.gaia.entity.Refugio;
import com.gaia.entity.SolicitudIngresoMascota;
import com.gaia.entity.User;
import org.springframework.stereotype.Component;

@Component
public class SolicitudIngresoMascotaMapper {

    private final UserMapper userMapper;

    public SolicitudIngresoMascotaMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public SolicitudIngresoMascota toEntity(SolicitudIngresoMascotaRequest request, User usuario, Refugio refugio, String imagenUrl) {
        return SolicitudIngresoMascota.builder()
                .usuario(usuario)
                .refugio(refugio)
                .nombreMascota(request.getNombreMascota())
                .edad(request.getEdad())
                .tipoEdad(request.getTipoEdad())
                .raza(request.getRaza())
                .tamano(request.getTamano())
                .genero(request.getGenero())
                .descripcion(request.getDescripcion())
                .estadoSalud(request.getEstadoSalud())
                .vacunado(request.getVacunado())
                .esterilizado(request.getEsterilizado())
                .motivoEntrega(request.getMotivoEntrega())
                .telefonoContacto(request.getTelefonoContacto())
                .direccion(request.getDireccion())
                .imagenUrl(imagenUrl)
                .estadoSolicitud(EstadoSolicitud.PENDIENTE)
                .build();
    }

    public SolicitudIngresoMascotaResponse toResponse(SolicitudIngresoMascota solicitud) {
        return new SolicitudIngresoMascotaResponse(
                solicitud.getId(),
                userMapper.toResponse(solicitud.getUsuario()),
                solicitud.getRefugio().getId(),
                solicitud.getRefugio().getNombre(),
                solicitud.getNombreMascota(),
                solicitud.getEdad(),
                solicitud.getTipoEdad(),
                solicitud.getRaza(),
                solicitud.getTamano(),
                solicitud.getGenero(),
                solicitud.getDescripcion(),
                solicitud.getEstadoSalud(),
                solicitud.getVacunado(),
                solicitud.getEsterilizado(),
                solicitud.getMotivoEntrega(),
                solicitud.getTelefonoContacto(),
                solicitud.getDireccion(),
                solicitud.getImagenUrl(),
                solicitud.getEstadoSolicitud(),
                solicitud.getFechaSolicitud()
        );
    }
}
