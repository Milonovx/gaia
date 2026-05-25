export interface Refugio {
  id: number;
  nombre: string;
  direccion: string;
  telefono?: string;
  descripcion?: string;
  latitud: number;
  longitud: number;
  ciudad: string;
}

export type RefugioRequest = Omit<Refugio, 'id'>;
