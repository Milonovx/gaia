import { Mascota } from './mascota.model';
import { User } from './user.model';

export type EstadoSolicitud = 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';

export interface SolicitudAdopcion {
  id: number;
  usuario: User;
  mascota: Mascota;
  mensaje: string;
  estado: EstadoSolicitud;
  fechaSolicitud: string;
}

export interface SolicitudAdopcionRequest {
  mascotaId: number;
  mensaje: string;
}

export interface ActualizarEstadoSolicitudRequest {
  estado: EstadoSolicitud;
}
