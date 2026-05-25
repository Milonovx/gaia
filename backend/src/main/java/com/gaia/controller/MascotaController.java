package com.gaia.controller;

import com.gaia.dto.mascota.MascotaRequest;
import com.gaia.dto.mascota.MascotaResponse;
import com.gaia.service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @Operation(summary = "Listar mascotas con filtros")
    @GetMapping
    public ResponseEntity<List<MascotaResponse>> findAll(
            @RequestParam(required = false) String raza,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Boolean disponible
    ) {
        return ResponseEntity.ok(mascotaService.findAll(raza, tamano, genero, disponible));
    }

    @Operation(summary = "Consultar mascota por id")
    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.findById(id));
    }

    @Operation(summary = "Crear mascota")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MascotaResponse> create(@Valid @RequestBody MascotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.create(request));
    }

    @Operation(summary = "Actualizar mascota")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponse> update(@PathVariable Long id, @Valid @RequestBody MascotaRequest request) {
        return ResponseEntity.ok(mascotaService.update(id, request));
    }

    @Operation(summary = "Eliminar mascota")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mascotaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
