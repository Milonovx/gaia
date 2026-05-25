import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'edadMascota',
  standalone: true
})
export class EdadMascotaPipe implements PipeTransform {
  transform(value?: { edad?: number | null; tipoEdad?: string | null }): string {
    const edad = value?.edad ?? 0;
    const tipo = value?.tipoEdad === 'MESES' ? 'mes' : 'año';
    const plural = edad === 1 ? '' : (tipo === 'mes' ? 'es' : 's');
    return `${edad} ${tipo}${plural}`;
  }
}
