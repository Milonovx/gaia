import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../environments/environment';
import { JwtResponse } from '../models/jwt-response.model';
import { LoginRequest } from '../models/login-request.model';
import { RegisterRequest } from '../models/register-request.model';
import { User } from '../models/user.model';
import { StorageService } from './storage.service';

interface JwtPayload {
  sub: string;
  exp: number;
  authorities?: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private readonly currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();
  isAuthenticated$ = this.currentUser$.pipe(map((user) => this.hasValidSession(user)));
  isAdmin$ = this.currentUser$.pipe(map((user) => this.hasValidSession(user) && user?.role === 'ROLE_ADMIN'));

  constructor(
    private readonly http: HttpClient,
    private readonly storage: StorageService,
    private readonly router: Router
  ) {
    this.restoreSession();
  }

  login(request: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => this.persistSession(response))
    );
  }

  register(request: RegisterRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/register`, request).pipe(
      tap((response) => this.persistSession(response))
    );
  }

  logout(redirect = true): void {
    this.storage.clear();
    this.currentUserSubject.next(null);
    if (redirect) {
      this.router.navigate(['/login']);
    }
  }

  updateCurrentUser(user: User): void {
    this.storage.setUser(user);
    this.currentUserSubject.next(user);
  }

  get token(): string | null {
    return this.storage.getToken();
  }

  get user(): User | null {
    return this.currentUserSubject.value;
  }

  isAuthenticated(): boolean {
    const authenticated = !!this.token && !!this.user && !this.isTokenExpired();
    if (!authenticated && this.token) {
      this.logout(false);
    }
    return authenticated;
  }

  isAdmin(): boolean {
    return this.isAuthenticated() && this.user?.role === 'ROLE_ADMIN';
  }

  isTokenExpired(): boolean {
    const token = this.token;
    if (!token) {
      return true;
    }
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      return decoded.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }

  private persistSession(response: JwtResponse): void {
    this.storage.setToken(response.token);
    this.storage.setUser(response.usuario);
    this.currentUserSubject.next(response.usuario);
  }

  private restoreSession(): void {
    const token = this.token;
    const user = this.storage.getUser();
    if (!token || !user || this.isTokenExpired()) {
      this.storage.clear();
      this.currentUserSubject.next(null);
      return;
    }
    this.currentUserSubject.next(user);
  }

  private hasValidSession(user: User | null): boolean {
    return !!user && !!this.token && !this.isTokenExpired();
  }
}
