import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { catchError, finalize, forkJoin, of } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { Refugio } from '../../models/refugio.model';
import { DashboardStats } from '../../models/dashboard.model';
import { MascotaService } from '../../services/mascota.service';
import { RefugioService } from '../../services/refugio.service';
import { DashboardService } from '../../services/dashboard.service';
import { AuthService } from '../../services/auth.service';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [EmptyStateComponent, FaIconComponent, LoadingSpinnerComponent, NgFor, NgIf, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  loading = true;
  featuredPets: Mascota[] = [];
  refugios: Refugio[] = [];
  stats: DashboardStats = {
    totalMascotas: 0,
    totalUsuarios: 0,
    solicitudesPendientes: 0,
    mascotasDisponibles: 0,
    solicitudesIngresoPendientes: 0
  };

  fallbackImage = 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&w=900&q=80';

  constructor(
    private readonly mascotaService: MascotaService,
    private readonly refugioService: RefugioService,
    private readonly dashboardService: DashboardService,
    public readonly auth: AuthService
  ) {}

  ngOnInit(): void {
    forkJoin({
      mascotas: this.mascotaService.getMascotas({ disponible: true }),
      refugios: this.refugioService.getRefugios()
    })
      .pipe(
        catchError((error) => {
          console.error('[GAIA Component] Home load failed', error);
          return of({ mascotas: [], refugios: [] });
        }),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: ({ mascotas, refugios }) => {
          this.featuredPets = mascotas.slice(0, 3);
          this.refugios = refugios;
          this.stats = {
            totalMascotas: mascotas.length,
            totalUsuarios: 0,
            solicitudesPendientes: 0,
            mascotasDisponibles: mascotas.filter((mascota) => mascota.disponible).length,
            solicitudesIngresoPendientes: 0
          };
        }
      });

    if (this.auth.isAdmin()) {
      this.dashboardService.getStats()
        .pipe(catchError((error) => {
          console.error('[GAIA Component] Home admin stats failed', error);
          return of(this.stats);
        }))
        .subscribe((stats) => this.stats = stats);
    }
  }
}
