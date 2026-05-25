import { Refugio } from './refugio.model';

export type TipoEdad = 'MESES' | 'ANIOS';
export type TamanoMascota = 'PEQUENO' | 'MEDIANO' | 'GRANDE';
export type GeneroMascota = 'MACHO' | 'HEMBRA';

export interface Mascota {
  id: number;
  nombre: string;
  edad: number;
  tipoEdad: TipoEdad;
  raza: string;
  tamano: TamanoMascota;
  genero: GeneroMascota;
  descripcion?: string;
  estadoSalud: string;
  vacunado: boolean;
  esterilizado: boolean;
  imagenUrl?: string;
  disponible: boolean;
  fechaRescate?: string;
  refugio: Refugio;
  createdAt: string;
}

export interface MascotaRequest {
  nombre: string;
  edad: number;
  tipoEdad: TipoEdad;
  raza: string;
  tamano: TamanoMascota;
  genero: GeneroMascota;
  descripcion?: string;
  estadoSalud: string;
  vacunado: boolean;
  esterilizado: boolean;
  imagenUrl?: string;
  disponible: boolean;
  fechaRescate?: string;
  refugioId: number;
}

export interface MascotaFilters {
  raza?: string;
  tamano?: string;
  genero?: string;
  disponible?: boolean | null;
}
