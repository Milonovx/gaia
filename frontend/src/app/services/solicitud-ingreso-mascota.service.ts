import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  ActualizarEstadoIngresoRequest,
  SolicitudIngresoMascota,
  SolicitudIngresoMascotaRequest
} from '../models/solicitud-ingreso-mascota.model';

@Injectable({ providedIn: 'root' })
export class SolicitudIngresoMascotaService {
  private readonly apiUrl = `${environment.apiUrl}/ingresos-mascotas`;

  constructor(private readonly http: HttpClient) {}

  create(request: SolicitudIngresoMascotaRequest): Observable<HttpEvent<SolicitudIngresoMascota>> {
    const formData = new FormData();
    formData.append('nombreMascota', request.nombreMascota);
    formData.append('edad', String(request.edad));
    formData.append('tipoEdad', request.tipoEdad);
    formData.append('raza', request.raza);
    formData.append('tamano', request.tamano);
    formData.append('genero', request.genero);
    formData.append('descripcion', request.descripcion);
    formData.append('estadoSalud', request.estadoSalud);
    formData.append('vacunado', String(request.vacunado));
    formData.append('esterilizado', String(request.esterilizado));
    formData.append('motivoEntrega', request.motivoEntrega);
    formData.append('telefonoContacto', request.telefonoContacto);
    formData.append('direccion', request.direccion);
    formData.append('refugioId', String(request.refugioId));
    formData.append('imagen', request.imagen);
    return this.http.post<SolicitudIngresoMascota>(this.apiUrl, formData, {
      observe: 'events',
      reportProgress: true
    });
  }

  findAll(): Observable<SolicitudIngresoMascota[]> {
    return this.http.get<SolicitudIngresoMascota[]>(this.apiUrl);
  }

  updateEstado(id: number, request: ActualizarEstadoIngresoRequest): Observable<SolicitudIngresoMascota> {
    return this.http.put<SolicitudIngresoMascota>(`${this.apiUrl}/${id}/estado`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
