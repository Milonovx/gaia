package com.gaia.dto.refugio;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record RefugioRequest(
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 180) String direccion,
        @Size(max = 30) String telefono,
        String descripcion,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitud,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitud,
        @NotBlank @Size(max = 80) String ciudad
) {
}
