package com.gaia.mapper;

import com.gaia.dto.mascota.MascotaRequest;
import com.gaia.dto.mascota.MascotaResponse;
import com.gaia.entity.Mascota;
import com.gaia.entity.Refugio;
import org.springframework.stereotype.Component;

@Component
public class MascotaMapper {

    private final RefugioMapper refugioMapper;

    public MascotaMapper(RefugioMapper refugioMapper) {
        this.refugioMapper = refugioMapper;
    }

    public Mascota toEntity(MascotaRequest request, Refugio refugio) {
        return Mascota.builder()
                .nombre(request.nombre())
                .edad(request.edad())
                .tipoEdad(request.tipoEdad())
                .raza(request.raza())
                .tamano(request.tamano())
                .genero(request.genero())
                .descripcion(request.descripcion())
                .estadoSalud(request.estadoSalud())
                .vacunado(request.vacunado())
                .esterilizado(request.esterilizado())
                .imagenUrl(request.imagenUrl())
                .disponible(request.disponible())
                .fechaRescate(request.fechaRescate())
                .refugio(refugio)
                .build();
    }

    public void updateEntity(Mascota mascota, MascotaRequest request, Refugio refugio) {
        mascota.setNombre(request.nombre());
        mascota.setEdad(request.edad());
        mascota.setTipoEdad(request.tipoEdad());
        mascota.setRaza(request.raza());
        mascota.setTamano(request.tamano());
        mascota.setGenero(request.genero());
        mascota.setDescripcion(request.descripcion());
        mascota.setEstadoSalud(request.estadoSalud());
        mascota.setVacunado(request.vacunado());
        mascota.setEsterilizado(request.esterilizado());
        mascota.setImagenUrl(request.imagenUrl());
        mascota.setDisponible(request.disponible());
        mascota.setFechaRescate(request.fechaRescate());
        mascota.setRefugio(refugio);
    }

    public MascotaResponse toResponse(Mascota mascota) {
        return new MascotaResponse(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEdad(),
                mascota.getTipoEdad(),
                mascota.getRaza(),
                mascota.getTamano(),
                mascota.getGenero(),
                mascota.getDescripcion(),
                mascota.getEstadoSalud(),
                mascota.getVacunado(),
                mascota.getEsterilizado(),
                mascota.getImagenUrl(),
                mascota.getDisponible(),
                mascota.getFechaRescate(),
                refugioMapper.toResponse(mascota.getRefugio()),
                mascota.getCreatedAt()
        );
    }
}
