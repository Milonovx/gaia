package com.gaia.service;

import com.gaia.dto.ingreso.ActualizarEstadoIngresoRequest;
import com.gaia.dto.ingreso.SolicitudIngresoMascotaRequest;
import com.gaia.dto.ingreso.SolicitudIngresoMascotaResponse;
import java.util.List;

public interface SolicitudIngresoMascotaService {

    SolicitudIngresoMascotaResponse create(SolicitudIngresoMascotaRequest request, String email);

    List<SolicitudIngresoMascotaResponse> findAll(String email);

    SolicitudIngresoMascotaResponse updateEstado(Long id, ActualizarEstadoIngresoRequest request);

    void delete(Long id);
}
