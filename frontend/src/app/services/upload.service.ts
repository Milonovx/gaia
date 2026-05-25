import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ImageUploadResponse } from '../models/upload.model';

@Injectable({ providedIn: 'root' })
export class UploadService {
  private readonly apiUrl = `${environment.apiUrl}/uploads/images`;

  constructor(private readonly http: HttpClient) {}

  uploadImage(file: File): Observable<HttpEvent<ImageUploadResponse>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ImageUploadResponse>(this.apiUrl, formData, {
      observe: 'events',
      reportProgress: true
    });
  }
}
