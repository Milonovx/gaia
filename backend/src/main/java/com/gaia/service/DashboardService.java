package com.gaia.service;

import com.gaia.dto.dashboard.DashboardStatsResponse;

public interface DashboardService {

    DashboardStatsResponse getStats();

    long totalMascotas();

    long totalUsuarios();

    long solicitudesPendientes();

    long mascotasDisponibles();
}
