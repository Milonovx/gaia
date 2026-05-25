package com.gaia.mapper;

import com.gaia.dto.refugio.RefugioRequest;
import com.gaia.dto.refugio.RefugioResponse;
import com.gaia.entity.Refugio;
import org.springframework.stereotype.Component;

@Component
public class RefugioMapper {

    public Refugio toEntity(RefugioRequest request) {
        return Refugio.builder()
                .nombre(request.nombre())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .descripcion(request.descripcion())
                .latitud(request.latitud())
                .longitud(request.longitud())
                .ciudad(request.ciudad())
                .build();
    }

    public void updateEntity(Refugio refugio, RefugioRequest request) {
        refugio.setNombre(request.nombre());
        refugio.setDireccion(request.direccion());
        refugio.setTelefono(request.telefono());
        refugio.setDescripcion(request.descripcion());
        refugio.setLatitud(request.latitud());
        refugio.setLongitud(request.longitud());
        refugio.setCiudad(request.ciudad());
    }

    public RefugioResponse toResponse(Refugio refugio) {
        return new RefugioResponse(
                refugio.getId(),
                refugio.getNombre(),
                refugio.getDireccion(),
                refugio.getTelefono(),
                refugio.getDescripcion(),
                refugio.getLatitud(),
                refugio.getLongitud(),
                refugio.getCiudad()
        );
    }
}
