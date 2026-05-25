import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Mascota, MascotaFilters, MascotaRequest } from '../models/mascota.model';

@Injectable({ providedIn: 'root' })
export class MascotaService {
  private readonly apiUrl = `${environment.apiUrl}/mascotas`;

  constructor(private readonly http: HttpClient) {}

  getMascotas(filters: MascotaFilters = {}): Observable<Mascota[]> {
    let params = new HttpParams();
    if (filters.raza) params = params.set('raza', filters.raza);
    if (filters.tamano) params = params.set('tamano', filters.tamano);
    if (filters.genero) params = params.set('genero', filters.genero);
    if (filters.disponible !== null && filters.disponible !== undefined) {
      params = params.set('disponible', filters.disponible);
    }
    return this.http.get<Mascota[]>(this.apiUrl, { params });
  }

  getMascota(id: number): Observable<Mascota> {
    return this.http.get<Mascota>(`${this.apiUrl}/${id}`);
  }

  createMascota(request: MascotaRequest): Observable<Mascota> {
    return this.http.post<Mascota>(this.apiUrl, request);
  }

  updateMascota(id: number, request: MascotaRequest): Observable<Mascota> {
    return this.http.put<Mascota>(`${this.apiUrl}/${id}`, request);
  }

  deleteMascota(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
