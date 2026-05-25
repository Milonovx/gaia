import { Component, Input } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [NgIf],
  templateUrl: './loading-spinner.component.html'
})
export class LoadingSpinnerComponent {
  @Input() label = 'Cargando...';
}
