package com.gaia.dto.refugio;

import java.math.BigDecimal;

public record RefugioResponse(
        Long id,
        String nombre,
        String direccion,
        String telefono,
        String descripcion,
        BigDecimal latitud,
        BigDecimal longitud,
        String ciudad
) {
}
