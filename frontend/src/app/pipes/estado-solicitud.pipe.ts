import { Pipe, PipeTransform } from '@angular/core';
import { EstadoSolicitud } from '../models/solicitud-adopcion.model';

@Pipe({
  name: 'estadoSolicitud',
  standalone: true
})
export class EstadoSolicitudPipe implements PipeTransform {
  transform(value: EstadoSolicitud): string {
    const labels: Record<EstadoSolicitud, string> = {
      PENDIENTE: 'Pendiente',
      APROBADA: 'Aprobada',
      RECHAZADA: 'Rechazada'
    };
    return labels[value] ?? value;
  }
}
