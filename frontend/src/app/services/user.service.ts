import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UpdateUserProfileRequest, UpdateUserRoleRequest, User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(private readonly http: HttpClient) {}

  me(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  updateMe(request: UpdateUserProfileRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me`, request);
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  updateRole(id: number, request: UpdateUserRoleRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/role`, request);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
