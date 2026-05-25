import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  ActualizarEstadoSolicitudRequest,
  SolicitudAdopcion,
  SolicitudAdopcionRequest
} from '../models/solicitud-adopcion.model';

@Injectable({ providedIn: 'root' })
export class AdopcionService {
  private readonly apiUrl = `${environment.apiUrl}/adopciones`;

  constructor(private readonly http: HttpClient) {}

  createSolicitud(request: SolicitudAdopcionRequest): Observable<SolicitudAdopcion> {
    return this.http.post<SolicitudAdopcion>(this.apiUrl, request);
  }

  getSolicitudes(): Observable<SolicitudAdopcion[]> {
    return this.http.get<SolicitudAdopcion[]>(this.apiUrl);
  }

  updateEstado(id: number, request: ActualizarEstadoSolicitudRequest): Observable<SolicitudAdopcion> {
    return this.http.put<SolicitudAdopcion>(`${this.apiUrl}/${id}/estado`, request);
  }

  deleteSolicitud(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
