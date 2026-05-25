package com.gaia.dto.ingreso;

import com.gaia.dto.user.UserResponse;
import com.gaia.entity.EstadoSolicitud;
import com.gaia.entity.GeneroMascota;
import com.gaia.entity.TamanoMascota;
import com.gaia.entity.TipoEdad;
import java.time.LocalDateTime;

public record SolicitudIngresoMascotaResponse(
        Long id,
        UserResponse usuario,
        Long refugioId,
        String refugioNombre,
        String nombreMascota,
        Integer edad,
        TipoEdad tipoEdad,
        String raza,
        TamanoMascota tamano,
        GeneroMascota genero,
        String descripcion,
        String estadoSalud,
        Boolean vacunado,
        Boolean esterilizado,
        String motivoEntrega,
        String telefonoContacto,
        String direccion,
        String imagenUrl,
        EstadoSolicitud estadoSolicitud,
        LocalDateTime fechaSolicitud
) {
}
