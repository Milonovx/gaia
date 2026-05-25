import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { ToastrService } from 'ngx-toastr';
import { EMPTY, catchError, finalize, switchMap, tap } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { AdopcionService } from '../../services/adopcion.service';
import { AuthService } from '../../services/auth.service';
import { MascotaService } from '../../services/mascota.service';
import { EdadMascotaPipe } from '../../pipes/edad-mascota.pipe';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-mascota-detalle',
  standalone: true,
  imports: [EdadMascotaPipe, FaIconComponent, LoadingSpinnerComponent, NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './mascota-detalle.component.html',
  styleUrl: './mascota-detalle.component.css'
})
export class MascotaDetalleComponent implements OnInit {
  loading = true;
  submitting = false;
  mascota?: Mascota;
  fallbackImage = 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=1200&q=80';

  form = this.fb.nonNullable.group({
    mensaje: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(2000)]]
  });

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly fb: FormBuilder,
    private readonly mascotaService: MascotaService,
    private readonly adopcionService: AdopcionService,
    public readonly auth: AuthService,
    private readonly toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        tap(() => {
          this.loading = true;
          this.mascota = undefined;
        }),
        switchMap((params) => {
          const id = Number(params.get('id'));
          if (!Number.isInteger(id) || id <= 0) {
            this.toastr.error('Mascota no encontrada');
            this.router.navigate(['/mascotas']);
            this.loading = false;
            return EMPTY;
          }
          return this.mascotaService.getMascota(id).pipe(
            finalize(() => this.loading = false)
          );
        }),
        catchError(() => {
          this.loading = false;
          this.toastr.error('No fue posible cargar la mascota');
          this.router.navigate(['/mascotas']);
          return EMPTY;
        })
      )
      .subscribe((mascota) => {
        this.mascota = mascota;
        this.form.patchValue({
          mensaje: `Hola, quiero solicitar la adopción de ${mascota.nombre}.`
        });
      });
  }

  labelTamano(value?: string): string {
    if (value === 'PEQUENO') return 'Pequeño';
    if (value === 'GRANDE') return 'Grande';
    return 'Mediano';
  }

  labelGenero(value?: string): string {
    return value === 'HEMBRA' ? 'Hembra' : 'Macho';
  }

  solicitarAdopcion(): void {
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    if (!this.mascota || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.adopcionService.createSolicitud({
      mascotaId: this.mascota.id,
      mensaje: this.form.getRawValue().mensaje
    })
      .pipe(finalize(() => this.submitting = false))
      .subscribe({
        next: () => {
          this.toastr.success('Solicitud enviada');
          this.router.navigate(['/solicitudes']);
        },
        error: (error) => console.error('[GAIA Component] Solicitud adopcion failed', error)
      });
  }
}
