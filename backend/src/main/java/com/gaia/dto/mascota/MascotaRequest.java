package com.gaia.dto.mascota;

import com.gaia.entity.GeneroMascota;
import com.gaia.entity.TamanoMascota;
import com.gaia.entity.TipoEdad;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MascotaRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotNull @Min(0) @Max(40) Integer edad,
        @NotNull TipoEdad tipoEdad,
        @NotBlank @Size(max = 80) String raza,
        @NotNull TamanoMascota tamano,
        @NotNull GeneroMascota genero,
        String descripcion,
        @NotBlank @Size(max = 120) String estadoSalud,
        @NotNull Boolean vacunado,
        @NotNull Boolean esterilizado,
        @Size(max = 500) String imagenUrl,
        @NotNull Boolean disponible,
        LocalDate fechaRescate,
        @NotNull Long refugioId
) {
}
