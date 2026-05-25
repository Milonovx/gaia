package com.gaia.dto.ingreso;

import com.gaia.entity.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoIngresoRequest(
        @NotNull EstadoSolicitud estadoSolicitud,
        Long refugioId
) {
}
