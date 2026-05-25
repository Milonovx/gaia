package com.gaia.dto.solicitud;

import com.gaia.entity.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoSolicitudRequest(
        @NotNull EstadoSolicitud estado
) {
}
