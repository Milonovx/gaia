package com.gaia.dto.dashboard;

public record DashboardStatsResponse(
        long totalMascotas,
        long totalUsuarios,
        long solicitudesPendientes,
        long mascotasDisponibles,
        long solicitudesIngresoPendientes
) {
}
