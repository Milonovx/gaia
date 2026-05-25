import { NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { catchError, finalize, of } from 'rxjs';
import { SolicitudAdopcion } from '../../models/solicitud-adopcion.model';
import { AdopcionService } from '../../services/adopcion.service';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';
import { EstadoSolicitudPipe } from '../../pipes/estado-solicitud.pipe';
import { EdadMascotaPipe } from '../../pipes/edad-mascota.pipe';

@Component({
  selector: 'app-solicitudes',
  standalone: true,
  imports: [EdadMascotaPipe, EmptyStateComponent, EstadoSolicitudPipe, FaIconComponent, LoadingSpinnerComponent, NgClass, NgFor, NgIf, RouterLink],
  templateUrl: './solicitudes.component.html',
  styleUrl: './solicitudes.component.css'
})
export class SolicitudesComponent implements OnInit {
  loading = true;
  solicitudes: SolicitudAdopcion[] = [];

  constructor(private readonly adopcionService: AdopcionService) {}

  ngOnInit(): void {
    this.adopcionService.getSolicitudes()
      .pipe(
        catchError((error) => {
          console.error('[GAIA Component] Solicitudes load failed', error);
          return of([]);
        }),
        finalize(() => this.loading = false)
      )
      .subscribe((solicitudes) => this.solicitudes = solicitudes);
  }
}
