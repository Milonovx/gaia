package com.gaia.service;

import com.gaia.dto.mascota.MascotaRequest;
import com.gaia.dto.mascota.MascotaResponse;
import java.util.List;

public interface MascotaService {

    List<MascotaResponse> findAll(String raza, String tamano, String genero, Boolean disponible);

    MascotaResponse findById(Long id);

    MascotaResponse create(MascotaRequest request);

    MascotaResponse update(Long id, MascotaRequest request);

    void delete(Long id);
}
