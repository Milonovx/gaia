package com.gaia.service;

import com.gaia.dto.solicitud.ActualizarEstadoSolicitudRequest;
import com.gaia.dto.solicitud.SolicitudAdopcionRequest;
import com.gaia.dto.solicitud.SolicitudAdopcionResponse;
import java.util.List;

public interface SolicitudAdopcionService {

    SolicitudAdopcionResponse create(SolicitudAdopcionRequest request, String email);

    List<SolicitudAdopcionResponse> findAll(String email);

    SolicitudAdopcionResponse updateEstado(Long id, ActualizarEstadoSolicitudRequest request);

    void delete(Long id);
}
