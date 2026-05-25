import { Component, Input } from '@angular/core';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { IconProp } from '@fortawesome/fontawesome-svg-core';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [FaIconComponent],
  templateUrl: './empty-state.component.html'
})
export class EmptyStateComponent {
  @Input() title = 'Sin resultados';
  @Input() message = 'No hay información disponible.';
  @Input() icon: IconProp = 'circle-info';
}
