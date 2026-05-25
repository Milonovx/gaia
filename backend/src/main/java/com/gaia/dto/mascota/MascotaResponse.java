package com.gaia.dto.mascota;

import com.gaia.dto.refugio.RefugioResponse;
import com.gaia.entity.GeneroMascota;
import com.gaia.entity.TamanoMascota;
import com.gaia.entity.TipoEdad;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MascotaResponse(
        Long id,
        String nombre,
        Integer edad,
        TipoEdad tipoEdad,
        String raza,
        TamanoMascota tamano,
        GeneroMascota genero,
        String descripcion,
        String estadoSalud,
        Boolean vacunado,
        Boolean esterilizado,
        String imagenUrl,
        Boolean disponible,
        LocalDate fechaRescate,
        RefugioResponse refugio,
        LocalDateTime createdAt
) {
}
