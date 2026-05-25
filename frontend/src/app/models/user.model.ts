import { Role } from './role.model';

export interface User {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono?: string;
  fotoPerfil?: string;
  role: Role;
  fechaCreacion: string;
}

export interface UpdateUserProfileRequest {
  nombre: string;
  apellido: string;
  telefono?: string;
  fotoPerfil?: string;
}

export interface UpdateUserRoleRequest {
  role: Role;
}
