package com.gaia.mapper;

import com.gaia.dto.solicitud.SolicitudAdopcionResponse;
import com.gaia.entity.SolicitudAdopcion;
import org.springframework.stereotype.Component;

@Component
public class SolicitudAdopcionMapper {

    private final UserMapper userMapper;
    private final MascotaMapper mascotaMapper;

    public SolicitudAdopcionMapper(UserMapper userMapper, MascotaMapper mascotaMapper) {
        this.userMapper = userMapper;
        this.mascotaMapper = mascotaMapper;
    }

    public SolicitudAdopcionResponse toResponse(SolicitudAdopcion solicitud) {
        return new SolicitudAdopcionResponse(
                solicitud.getId(),
                userMapper.toResponse(solicitud.getUsuario()),
                mascotaMapper.toResponse(solicitud.getMascota()),
                solicitud.getMensaje(),
                solicitud.getEstado(),
                solicitud.getFechaSolicitud()
        );
    }
}
