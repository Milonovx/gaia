package com.gaia.dto.solicitud;

import com.gaia.dto.mascota.MascotaResponse;
import com.gaia.dto.user.UserResponse;
import com.gaia.entity.EstadoSolicitud;
import java.time.LocalDateTime;

public record SolicitudAdopcionResponse(
        Long id,
        UserResponse usuario,
        MascotaResponse mascota,
        String mensaje,
        EstadoSolicitud estado,
        LocalDateTime fechaSolicitud
) {
}
