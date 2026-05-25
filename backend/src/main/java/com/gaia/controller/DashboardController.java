package com.gaia.controller;

import com.gaia.dto.dashboard.DashboardStatsResponse;
import com.gaia.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Resumen completo del dashboard")
    @GetMapping
    public ResponseEntity<DashboardStatsResponse> stats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @Operation(summary = "Total mascotas")
    @GetMapping("/total-mascotas")
    public ResponseEntity<Map<String, Long>> totalMascotas() {
        return ResponseEntity.ok(Map.of("totalMascotas", dashboardService.totalMascotas()));
    }

    @Operation(summary = "Total usuarios")
    @GetMapping("/total-usuarios")
    public ResponseEntity<Map<String, Long>> totalUsuarios() {
        return ResponseEntity.ok(Map.of("totalUsuarios", dashboardService.totalUsuarios()));
    }

    @Operation(summary = "Solicitudes pendientes")
    @GetMapping("/solicitudes-pendientes")
    public ResponseEntity<Map<String, Long>> solicitudesPendientes() {
        return ResponseEntity.ok(Map.of("solicitudesPendientes", dashboardService.solicitudesPendientes()));
    }

    @Operation(summary = "Mascotas disponibles")
    @GetMapping("/mascotas-disponibles")
    public ResponseEntity<Map<String, Long>> mascotasDisponibles() {
        return ResponseEntity.ok(Map.of("mascotasDisponibles", dashboardService.mascotasDisponibles()));
    }
}
