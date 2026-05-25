import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Refugio, RefugioRequest } from '../models/refugio.model';

@Injectable({ providedIn: 'root' })
export class RefugioService {
  private readonly apiUrl = `${environment.apiUrl}/refugios`;

  constructor(private readonly http: HttpClient) {}

  getRefugios(): Observable<Refugio[]> {
    return this.http.get<Refugio[]>(this.apiUrl);
  }

  getRefugio(id: number): Observable<Refugio> {
    return this.http.get<Refugio>(`${this.apiUrl}/${id}`);
  }

  createRefugio(request: RefugioRequest): Observable<Refugio> {
    return this.http.post<Refugio>(this.apiUrl, request);
  }

  updateRefugio(id: number, request: RefugioRequest): Observable<Refugio> {
    return this.http.put<Refugio>(`${this.apiUrl}/${id}`, request);
  }

  deleteRefugio(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
