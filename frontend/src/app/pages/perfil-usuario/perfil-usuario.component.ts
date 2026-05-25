import { NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpEventType } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { catchError, finalize, forkJoin, of } from 'rxjs';
import { SolicitudAdopcion } from '../../models/solicitud-adopcion.model';
import { SolicitudIngresoMascota } from '../../models/solicitud-ingreso-mascota.model';
import { AdopcionService } from '../../services/adopcion.service';
import { AuthService } from '../../services/auth.service';
import { SolicitudIngresoMascotaService } from '../../services/solicitud-ingreso-mascota.service';
import { UploadService } from '../../services/upload.service';
import { UserService } from '../../services/user.service';
import { EstadoSolicitudPipe } from '../../pipes/estado-solicitud.pipe';
import { EdadMascotaPipe } from '../../pipes/edad-mascota.pipe';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';
import { revokeObjectUrl, validateImageFile } from '../../shared/image-file.utils';
import { ToastrService } from 'ngx-toastr';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-perfil-usuario',
  standalone: true,
  imports: [EdadMascotaPipe, EstadoSolicitudPipe, FaIconComponent, LoadingSpinnerComponent, NgClass, NgFor, NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './perfil-usuario.component.html',
  styleUrl: './perfil-usuario.component.css'
})
export class PerfilUsuarioComponent implements OnInit, OnDestroy {
  user: User | null = this.auth.user;
  loading = true;
  solicitudesAdopcion: SolicitudAdopcion[] = [];
  solicitudesIngreso: SolicitudIngresoMascota[] = [];
  editing = false;
  saving = false;
  uploading = false;
  uploadProgress = 0;
  profilePreviewUrl?: string;
  profileForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(80)]],
    apellido: ['', [Validators.required, Validators.maxLength(80)]],
    telefono: [''],
    fotoPerfil: ['']
  });

  constructor(
    public readonly auth: AuthService,
    private readonly fb: FormBuilder,
    private readonly adopcionService: AdopcionService,
    private readonly ingresoService: SolicitudIngresoMascotaService,
    private readonly uploadService: UploadService,
    private readonly userService: UserService,
    private readonly toastr: ToastrService
  ) {}

  ngOnInit(): void {
    forkJoin({
      user: this.userService.me(),
      adopciones: this.adopcionService.getSolicitudes(),
      ingresos: this.ingresoService.findAll()
    })
      .pipe(
        catchError((error) => {
          console.error('[GAIA Component] Perfil load failed', error);
          return of({ user: this.user, adopciones: [], ingresos: [] });
        }),
        finalize(() => this.loading = false)
      )
      .subscribe(({ user, adopciones, ingresos }) => {
        if (user) {
          this.user = user;
          this.auth.updateCurrentUser(user);
        }
        this.solicitudesAdopcion = adopciones;
        this.solicitudesIngreso = ingresos;
        this.patchProfileForm();
      });
  }

  ngOnDestroy(): void {
    revokeObjectUrl(this.profilePreviewUrl);
  }

  startEdit(): void {
    this.patchProfileForm();
    this.profilePreviewUrl = this.user?.fotoPerfil || undefined;
    this.editing = true;
  }

  cancelEdit(): void {
    this.editing = false;
    revokeObjectUrl(this.profilePreviewUrl);
    this.profilePreviewUrl = undefined;
    this.patchProfileForm();
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.userService.updateMe(this.profileForm.getRawValue())
      .pipe(finalize(() => this.saving = false))
      .subscribe({
        next: (user) => {
          this.auth.updateCurrentUser(user);
          this.user = user;
          this.profilePreviewUrl = user.fotoPerfil || undefined;
          this.editing = false;
          this.toastr.success('Perfil actualizado');
        },
        error: (error) => console.error('[GAIA Component] Perfil save failed', error)
      });
  }

  onProfileImageSelected(event: Event): void {
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
    revokeObjectUrl(this.profilePreviewUrl);
    this.profilePreviewUrl = URL.createObjectURL(file);
    this.uploading = true;
    this.uploadProgress = 0;
    this.uploadService.uploadImage(file)
      .pipe(finalize(() => this.uploading = false))
      .subscribe({
        next: (uploadEvent) => {
          if (uploadEvent.type === HttpEventType.UploadProgress && uploadEvent.total) {
            this.uploadProgress = Math.round((uploadEvent.loaded / uploadEvent.total) * 100);
          }
          if (uploadEvent.type === HttpEventType.Response && uploadEvent.body) {
            this.profileForm.patchValue({ fotoPerfil: uploadEvent.body.url });
            this.toastr.success('Foto subida');
          }
        },
        error: (error) => {
          console.error('[GAIA Component] Perfil image upload failed', error);
          this.profileForm.patchValue({ fotoPerfil: this.user?.fotoPerfil || '' });
          revokeObjectUrl(this.profilePreviewUrl);
          this.profilePreviewUrl = this.user?.fotoPerfil || undefined;
          input.value = '';
        }
      });
  }

  labelTamano(value?: string): string {
    if (value === 'PEQUENO') return 'Pequeño';
    if (value === 'GRANDE') return 'Grande';
    return 'Mediano';
  }

  private patchProfileForm(): void {
    if (!this.user) {
      return;
    }
    this.profileForm.patchValue({
      nombre: this.user.nombre,
      apellido: this.user.apellido,
      telefono: this.user.telefono || '',
      fotoPerfil: this.user.fotoPerfil || ''
    });
  }
}
