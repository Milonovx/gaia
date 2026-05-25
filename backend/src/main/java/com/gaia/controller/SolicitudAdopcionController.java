package com.gaia.controller;

import com.gaia.dto.solicitud.ActualizarEstadoSolicitudRequest;
import com.gaia.dto.solicitud.SolicitudAdopcionRequest;
import com.gaia.dto.solicitud.SolicitudAdopcionResponse;
import com.gaia.service.SolicitudAdopcionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/adopciones")
public class SolicitudAdopcionController {

    private final SolicitudAdopcionService solicitudService;

    public SolicitudAdopcionController(SolicitudAdopcionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @Operation(summary = "Crear solicitud de adopción")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<SolicitudAdopcionResponse> create(
            @Valid @RequestBody SolicitudAdopcionRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.create(request, principal.getName()));
    }

    @Operation(summary = "Listar solicitudes de adopción")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<SolicitudAdopcionResponse>> findAll(Principal principal) {
        return ResponseEntity.ok(solicitudService.findAll(principal.getName()));
    }

    @Operation(summary = "Actualizar estado de solicitud")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudAdopcionResponse> updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoSolicitudRequest request
    ) {
        return ResponseEntity.ok(solicitudService.updateEstado(id, request));
    }

    @Operation(summary = "Eliminar solicitud de adopcion")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        solicitudService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
