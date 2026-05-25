import { AfterViewInit, Component, OnDestroy } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import * as L from 'leaflet';
import { catchError, finalize, of } from 'rxjs';
import { Refugio } from '../../models/refugio.model';
import { RefugioService } from '../../services/refugio.service';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-refugios',
  standalone: true,
  imports: [FaIconComponent, LoadingSpinnerComponent, NgFor, NgIf],
  templateUrl: './refugios.component.html',
  styleUrl: './refugios.component.css'
})
export class RefugiosComponent implements AfterViewInit, OnDestroy {
  loading = true;
  refugios: Refugio[] = [];
  private map?: L.Map;

  constructor(private readonly refugioService: RefugioService) {}

  ngAfterViewInit(): void {
    this.initMap();
    this.loadRefugios();
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private initMap(): void {
    const icon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });
    L.Marker.prototype.options.icon = icon;

    this.map = L.map('refugiosMap', {
      center: [2.9345, -75.2875],
      zoom: 13,
      scrollWheelZoom: false
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);
  }

  private loadRefugios(): void {
    this.refugioService.getRefugios()
      .pipe(
        catchError((error) => {
          console.error('[GAIA Component] Refugios load failed', error);
          return of([]);
        }),
        finalize(() => this.loading = false)
      )
      .subscribe((refugios) => {
        this.refugios = refugios;
        this.renderMarkers();
      });
  }

  private renderMarkers(): void {
    if (!this.map) {
      return;
    }

    const bounds: L.LatLngTuple[] = [];
    this.refugios.forEach((refugio) => {
      const position: L.LatLngTuple = [Number(refugio.latitud), Number(refugio.longitud)];
      bounds.push(position);
      L.marker(position)
        .addTo(this.map!)
        .bindPopup(`
          <strong>${this.escapeHtml(refugio.nombre)}</strong><br>
          ${this.escapeHtml(refugio.direccion)}<br>
          ${this.escapeHtml(refugio.descripcion || '')}<br>
          ${this.escapeHtml(refugio.telefono || '')}
        `);
    });

    if (bounds.length) {
      this.map.fitBounds(bounds, { padding: [32, 32], maxZoom: 14 });
    }
  }

  private escapeHtml(value: string): string {
    return value.replace(/[&<>"']/g, (character) => {
      const entities: Record<string, string> = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
      };
      return entities[character];
    });
  }
}
