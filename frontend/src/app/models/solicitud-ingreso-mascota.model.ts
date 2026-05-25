import { EstadoSolicitud } from './solicitud-adopcion.model';
import { GeneroMascota, TamanoMascota, TipoEdad } from './mascota.model';
import { User } from './user.model';

export interface SolicitudIngresoMascota {
  id: number;
  usuario: User;
  refugioId: number;
  refugioNombre: string;
  nombreMascota: string;
  edad: number;
  tipoEdad: TipoEdad;
  raza: string;
  tamano: TamanoMascota;
  genero: GeneroMascota;
  descripcion: string;
  estadoSalud: string;
  vacunado: boolean;
  esterilizado: boolean;
  motivoEntrega: string;
  telefonoContacto: string;
  direccion: string;
  imagenUrl: string;
  estadoSolicitud: EstadoSolicitud;
  fechaSolicitud: string;
}

export interface SolicitudIngresoMascotaRequest {
  nombreMascota: string;
  edad: number;
  tipoEdad: TipoEdad;
  raza: string;
  tamano: TamanoMascota;
  genero: GeneroMascota;
  descripcion: string;
  estadoSalud: string;
  vacunado: boolean;
  esterilizado: boolean;
  motivoEntrega: string;
  telefonoContacto: string;
  direccion: string;
  refugioId: number;
  imagen: File;
}

export interface ActualizarEstadoIngresoRequest {
  estadoSolicitud: EstadoSolicitud;
  refugioId?: number;
}
