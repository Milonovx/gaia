import { HttpEventType } from '@angular/common/http';
import { NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { ToastrService } from 'ngx-toastr';
import { catchError, finalize, of } from 'rxjs';
import { Refugio } from '../../models/refugio.model';
import { RefugioService } from '../../services/refugio.service';
import { SolicitudIngresoMascotaService } from '../../services/solicitud-ingreso-mascota.service';
import { revokeObjectUrl, validateImageFile } from '../../shared/image-file.utils';

@Component({
  selector: 'app-entregar-mascota',
  standalone: true,
  imports: [FaIconComponent, NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './entregar-mascota.component.html',
  styleUrl: './entregar-mascota.component.css'
})
export class EntregarMascotaComponent implements OnInit, OnDestroy {
  loading = false;
  submitting = false;
  uploadProgress = 0;
  previewUrl?: string;
  selectedFile?: File;
  refugios: Refugio[] = [];

  form = this.fb.nonNullable.group({
    nombreMascota: ['', [Validators.required, Validators.maxLength(100)]],
    edad: [1, [Validators.required, Validators.min(0), Validators.max(40)]],
    tipoEdad: ['ANIOS' as 'MESES' | 'ANIOS', [Validators.required]],
    raza: ['', [Validators.required, Validators.maxLength(80)]],
    tamano: ['MEDIANO' as 'PEQUENO' | 'MEDIANO' | 'GRANDE', [Validators.required]],
    genero: ['MACHO' as 'MACHO' | 'HEMBRA', [Validators.required]],
    descripcion: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(2000)]],
    estadoSalud: ['Saludable', [Validators.required, Validators.maxLength(120)]],
    vacunado: [false],
    esterilizado: [false],
    motivoEntrega: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(2000)]],
    telefonoContacto: ['', [Validators.required, Validators.maxLength(30)]],
    direccion: ['', [Validators.required, Validators.maxLength(180)]],
    refugioId: [0, [Validators.required, Validators.min(1)]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly refugioService: RefugioService,
    private readonly ingresoService: SolicitudIngresoMascotaService,
    private readonly toastr: ToastrService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.refugioService.getRefugios()
      .pipe(
        catchError((error) => {
          console.error('[GAIA Component] Entregar mascota refugios load failed', error);
          return of([]);
        }),
        finalize(() => this.loading = false)
      )
      .subscribe((refugios) => {
        this.refugios = refugios;
        if (refugios.length) {
          this.form.patchValue({ refugioId: refugios[0].id });
        }
      });
  }

  ngOnDestroy(): void {
    revokeObjectUrl(this.previewUrl);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    const validationError = validateImageFile(file);
    if (validationError) {
      this.toastr.error(validationError);
      input.value = '';
      return;
    }

    revokeObjectUrl(this.previewUrl);
    this.selectedFile = file;
    this.previewUrl = URL.createObjectURL(file);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (!this.selectedFile) {
      this.toastr.error('Sube una imagen real de la mascota');
      return;
    }

    this.submitting = true;
    this.uploadProgress = 0;
    this.ingresoService.create({
      ...this.form.getRawValue(),
      imagen: this.selectedFile
    })
      .pipe(finalize(() => this.submitting = false))
      .subscribe({
        next: (event) => {
          if (event.type === HttpEventType.UploadProgress && event.total) {
            this.uploadProgress = Math.round((event.loaded / event.total) * 100);
          }
          if (event.type === HttpEventType.Response) {
            this.toastr.success('Solicitud enviada');
            this.router.navigate(['/perfil']);
          }
        },
        error: (error) => console.error('[GAIA Component] Entregar mascota submit failed', error)
      });
  }
}
