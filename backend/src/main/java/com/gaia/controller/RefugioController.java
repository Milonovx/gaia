package com.gaia.controller;

import com.gaia.dto.refugio.RefugioRequest;
import com.gaia.dto.refugio.RefugioResponse;
import com.gaia.service.RefugioService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refugios")
public class RefugioController {

    private final RefugioService refugioService;

    public RefugioController(RefugioService refugioService) {
        this.refugioService = refugioService;
    }

    @Operation(summary = "Listar refugios")
    @GetMapping
    public ResponseEntity<List<RefugioResponse>> findAll() {
        return ResponseEntity.ok(refugioService.findAll());
    }

    @Operation(summary = "Consultar refugio por id")
    @GetMapping("/{id}")
    public ResponseEntity<RefugioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(refugioService.findById(id));
    }

    @Operation(summary = "Crear refugio")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RefugioResponse> create(@Valid @RequestBody RefugioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(refugioService.create(request));
    }

    @Operation(summary = "Actualizar refugio")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RefugioResponse> update(@PathVariable Long id, @Valid @RequestBody RefugioRequest request) {
        return ResponseEntity.ok(refugioService.update(id, request));
    }

    @Operation(summary = "Eliminar refugio")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        refugioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
