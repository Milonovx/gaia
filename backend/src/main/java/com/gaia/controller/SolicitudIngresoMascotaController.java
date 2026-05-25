package com.gaia.controller;

import com.gaia.dto.ingreso.ActualizarEstadoIngresoRequest;
import com.gaia.dto.ingreso.SolicitudIngresoMascotaRequest;
import com.gaia.dto.ingreso.SolicitudIngresoMascotaResponse;
import com.gaia.service.SolicitudIngresoMascotaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingresos-mascotas")
public class SolicitudIngresoMascotaController {

    private final SolicitudIngresoMascotaService solicitudService;

    public SolicitudIngresoMascotaController(SolicitudIngresoMascotaService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @Operation(summary = "Crear solicitud de ingreso de mascota")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SolicitudIngresoMascotaResponse> create(
            @Valid @ModelAttribute SolicitudIngresoMascotaRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.create(request, principal.getName()));
    }

    @Operation(summary = "Listar solicitudes de ingreso")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<SolicitudIngresoMascotaResponse>> findAll(Principal principal) {
        return ResponseEntity.ok(solicitudService.findAll(principal.getName()));
    }

    @Operation(summary = "Actualizar estado de solicitud de ingreso")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudIngresoMascotaResponse> updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoIngresoRequest request
    ) {
        return ResponseEntity.ok(solicitudService.updateEstado(id, request));
    }

    @Operation(summary = "Eliminar solicitud de ingreso")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        solicitudService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
