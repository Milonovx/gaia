package com.gaia.dto.solicitud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitudAdopcionRequest(
        @NotNull Long mascotaId,
        @NotBlank @Size(min = 10, max = 2000) String mensaje
) {
}
