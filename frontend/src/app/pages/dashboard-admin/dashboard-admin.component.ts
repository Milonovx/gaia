import { HttpEventType } from '@angular/common/http';
import { DatePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { ToastrService } from 'ngx-toastr';
import { catchError, finalize, forkJoin, of } from 'rxjs';
import { DashboardStats } from '../../models/dashboard.model';
import { Mascota, MascotaRequest } from '../../models/mascota.model';
import { Refugio, RefugioRequest } from '../../models/refugio.model';
import { SolicitudAdopcion } from '../../models/solicitud-adopcion.model';
import { SolicitudIngresoMascota } from '../../models/solicitud-ingreso-mascota.model';
import { User } from '../../models/user.model';
import { AdopcionService } from '../../services/adopcion.service';
import { AuthService } from '../../services/auth.service';
import { DashboardService } from '../../services/dashboard.service';
import { MascotaService } from '../../services/mascota.service';
import { RefugioService } from '../../services/refugio.service';
import { SolicitudIngresoMascotaService } from '../../services/solicitud-ingreso-mascota.service';
import { UploadService } from '../../services/upload.service';
import { UserService } from '../../services/user.service';
import { EdadMascotaPipe } from '../../pipes/edad-mascota.pipe';
import { EstadoSolicitudPipe } from '../../pipes/estado-solicitud.pipe';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { revokeObjectUrl, validateImageFile } from '../../shared/image-file.utils';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';

type AdminTab = 'mascotas' | 'refugios' | 'solicitudes' | 'ingresos' | 'usuarios';

interface ConfirmDialog {
  title: string;
  message: string;
  confirmLabel: string;
  danger?: boolean;
  action: () => void;
}

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [DatePipe, EdadMascotaPipe, EmptyStateComponent, EstadoSolicitudPipe, FaIconComponent, LoadingSpinnerComponent, NgClass, NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './dashboard-admin.component.html',
  styleUrl: './dashboard-admin.component.css'
})
export class DashboardAdminComponent implements OnInit, OnDestroy {
  loading = true;
  savingMascota = false;
  savingRefugio = false;
  activeTab: AdminTab = 'mascotas';
  uploadingMascotaImage = false;
  uploadProgress = 0;
  mascotaPreviewUrl?: string;
  adminSearch = '';
  page = 1;
  pageSize = 8;
  confirmDialog?: ConfirmDialog;

  stats: DashboardStats = {
    totalMascotas: 0,
    totalUsuarios: 0,
    solicitudesPendientes: 0,
    mascotasDisponibles: 0,
    solicitudesIngresoPendientes: 0
  };
  mascotas: Mascota[] = [];
  refugios: Refugio[] = [];
  solicitudes: SolicitudAdopcion[] = [];
  solicitudesIngreso: SolicitudIngresoMascota[] = [];
  usuarios: User[] = [];
  editingMascotaId: number | null = null;
  editingRefugioId: number | null = null;

  mascotaForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    edad: [1, [Validators.required, Validators.min(0)]],
    tipoEdad: ['ANIOS' as 'MESES' | 'ANIOS', [Validators.required]],
    raza: ['', [Validators.required, Validators.maxLength(80)]],
    tamano: ['MEDIANO' as 'PEQUENO' | 'MEDIANO' | 'GRANDE', [Validators.required]],
    genero: ['MACHO' as 'MACHO' | 'HEMBRA', [Validators.required]],
    descripcion: [''],
    estadoSalud: ['Saludable', [Validators.required, Validators.maxLength(120)]],
    vacunado: [true],
    esterilizado: [false],
    imagenUrl: ['', [Validators.required]],
    disponible: [true],
    fechaRescate: [''],
    refugioId: [0, [Validators.required, Validators.min(1)]]
  });

  refugioForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
    direccion: ['', [Validators.required, Validators.maxLength(180)]],
    telefono: [''],
    descripcion: [''],
    latitud: [2.9345, [Validators.required]],
    longitud: [-75.2875, [Validators.required]],
    ciudad: ['Neiva', [Validators.required]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly dashboardService: DashboardService,
    private readonly mascotaService: MascotaService,
    private readonly refugioService: RefugioService,
    private readonly adopcionService: AdopcionService,
    private readonly ingresoService: SolicitudIngresoMascotaService,
    private readonly uploadService: UploadService,
    private readonly userService: UserService,
    public readonly auth: AuthService,
    private readonly toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  ngOnDestroy(): void {
    revokeObjectUrl(this.mascotaPreviewUrl);
  }

  get filteredMascotas(): Mascota[] {
    return this.filterRows(this.mascotas, (m) => [m.nombre, m.raza, m.tamano, m.genero, m.estadoSalud, m.refugio?.nombre, m.disponible ? 'disponible' : 'no disponible']);
  }

  get filteredRefugios(): Refugio[] {
    return this.filterRows(this.refugios, (r) => [r.nombre, r.direccion, r.ciudad, r.telefono || '']);
  }

  get filteredSolicitudes(): SolicitudAdopcion[] {
    return this.filterRows(this.solicitudes, (s) => [s.mascota.nombre, s.usuario.nombre, s.usuario.apellido, s.usuario.email, s.estado, s.mensaje]);
  }

  get filteredIngresos(): SolicitudIngresoMascota[] {
    return this.filterRows(this.solicitudesIngreso, (s) => [s.nombreMascota, s.raza, s.tamano, s.genero, s.usuario.email, s.estadoSolicitud]);
  }

  get filteredUsuarios(): User[] {
    return this.filterRows(this.usuarios, (u) => [u.nombre, u.apellido, u.email, u.role, u.telefono || '']);
  }

  get activeTotal(): number {
    return this.activeRows().length;
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.activeTotal / this.pageSize));
  }

  loadDashboard(): void {
    this.loading = true;
    forkJoin({
      stats: this.dashboardService.getStats(),
      mascotas: this.mascotaService.getMascotas(),
      refugios: this.refugioService.getRefugios(),
      solicitudes: this.adopcionService.getSolicitudes(),
      ingresos: this.ingresoService.findAll(),
      usuarios: this.userService.getUsers()
    })
      .pipe(
        catchError((error) => {
          console.error('[GAIA Component] Admin dashboard load failed', error);
          return of({
            stats: this.stats,
            mascotas: [],
            refugios: [],
            solicitudes: [],
            ingresos: [],
            usuarios: []
          });
        }),
        finalize(() => this.loading = false)
      )
      .subscribe(({ stats, mascotas, refugios, solicitudes, ingresos, usuarios }) => {
        this.stats = stats;
        this.mascotas = mascotas;
        this.refugios = refugios;
        this.solicitudes = solicitudes;
        this.solicitudesIngreso = ingresos;
        this.usuarios = usuarios;
        this.page = Math.min(this.page, this.totalPages);
        if (!this.mascotaForm.controls.refugioId.value && refugios.length) {
          this.mascotaForm.patchValue({ refugioId: refugios[0].id });
        }
      });
  }

  onMascotaImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    const validationError = validateImageFile(file);
    if (validationError) {
      this.toastr.error(validationError);
      input.value = '';
      return;
    }
    revokeObjectUrl(this.mascotaPreviewUrl);
    this.mascotaPreviewUrl = URL.createObjectURL(file);
    this.uploadingMascotaImage = true;
    this.uploadProgress = 0;
    this.uploadService.uploadImage(file)
      .pipe(finalize(() => this.uploadingMascotaImage = false))
      .subscribe({
        next: (uploadEvent) => {
          if (uploadEvent.type === HttpEventType.UploadProgress && uploadEvent.total) {
            this.uploadProgress = Math.round((uploadEvent.loaded / uploadEvent.total) * 100);
          }
          if (uploadEvent.type === HttpEventType.Response && uploadEvent.body) {
            this.mascotaForm.patchValue({ imagenUrl: uploadEvent.body.url });
            this.toastr.success('Imagen subida');
          }
        },
        error: (error) => {
          console.error('[GAIA Component] Admin image upload failed', error);
          this.clearMascotaUpload();
          input.value = '';
        }
      });
  }

  saveMascota(): void {
    if (this.mascotaForm.invalid) {
      this.mascotaForm.markAllAsTouched();
      return;
    }
    this.savingMascota = true;
    const request = this.buildMascotaRequest();
    const operation = this.editingMascotaId
      ? this.mascotaService.updateMascota(this.editingMascotaId, request)
      : this.mascotaService.createMascota(request);

    operation.pipe(finalize(() => this.savingMascota = false)).subscribe({
      next: () => {
        this.toastr.success(this.editingMascotaId ? 'Mascota actualizada' : 'Mascota creada');
        this.resetMascotaForm();
        this.loadDashboard();
      },
      error: (error) => console.error('[GAIA Component] Admin save mascota failed', error)
    });
  }

  editMascota(mascota: Mascota): void {
    this.editingMascotaId = mascota.id;
    this.mascotaForm.patchValue({
      nombre: mascota.nombre,
      edad: mascota.edad,
      tipoEdad: mascota.tipoEdad || 'ANIOS',
      raza: mascota.raza,
      tamano: mascota.tamano,
      genero: mascota.genero,
      descripcion: mascota.descripcion || '',
      estadoSalud: mascota.estadoSalud,
      vacunado: mascota.vacunado,
      esterilizado: mascota.esterilizado,
      imagenUrl: mascota.imagenUrl || '',
      disponible: mascota.disponible,
      fechaRescate: mascota.fechaRescate || '',
      refugioId: mascota.refugio.id
    });
    this.mascotaPreviewUrl = mascota.imagenUrl || undefined;
    this.setTab('mascotas', false);
  }

  deleteMascota(id: number): void {
    this.openConfirm('Eliminar mascota', 'Se eliminará la mascota y sus solicitudes asociadas.', 'Eliminar', true, () => {
      this.mascotaService.deleteMascota(id).subscribe({
        next: () => {
          this.toastr.success('Mascota eliminada');
          this.loadDashboard();
        },
        error: (error) => console.error('[GAIA Component] Admin delete mascota failed', error)
      });
    });
  }

  resetMascotaForm(): void {
    this.editingMascotaId = null;
    this.mascotaForm.reset({
      nombre: '',
      edad: 1,
      tipoEdad: 'ANIOS',
      raza: '',
      tamano: 'MEDIANO',
      genero: 'MACHO',
      descripcion: '',
      estadoSalud: 'Saludable',
      vacunado: true,
      esterilizado: false,
      imagenUrl: '',
      disponible: true,
      fechaRescate: '',
      refugioId: this.refugios[0]?.id || 0
    });
    this.mascotaPreviewUrl = undefined;
    this.uploadProgress = 0;
  }

  clearMascotaUpload(): void {
    revokeObjectUrl(this.mascotaPreviewUrl);
    this.mascotaPreviewUrl = undefined;
    this.uploadProgress = 0;
    this.mascotaForm.patchValue({ imagenUrl: '' });
  }

  saveRefugio(): void {
    if (this.refugioForm.invalid) {
      this.refugioForm.markAllAsTouched();
      return;
    }
    this.savingRefugio = true;
    const request = this.refugioForm.getRawValue() as RefugioRequest;
    const operation = this.editingRefugioId
      ? this.refugioService.updateRefugio(this.editingRefugioId, request)
      : this.refugioService.createRefugio(request);

    operation.pipe(finalize(() => this.savingRefugio = false)).subscribe({
      next: () => {
        this.toastr.success(this.editingRefugioId ? 'Refugio actualizado' : 'Refugio creado');
        this.resetRefugioForm();
        this.loadDashboard();
      },
      error: (error) => console.error('[GAIA Component] Admin save refugio failed', error)
    });
  }

  editRefugio(refugio: Refugio): void {
    this.editingRefugioId = refugio.id;
    this.refugioForm.patchValue({
      nombre: refugio.nombre,
      direccion: refugio.direccion,
      telefono: refugio.telefono || '',
      descripcion: refugio.descripcion || '',
      latitud: Number(refugio.latitud),
      longitud: Number(refugio.longitud),
      ciudad: refugio.ciudad
    });
    this.setTab('refugios', false);
  }

  deleteRefugio(id: number): void {
    this.openConfirm('Eliminar refugio', 'Se eliminará el refugio seleccionado si no tiene restricciones activas.', 'Eliminar', true, () => {
      this.refugioService.deleteRefugio(id).subscribe({
        next: () => {
          this.toastr.success('Refugio eliminado');
          this.loadDashboard();
        },
        error: (error) => console.error('[GAIA Component] Admin delete refugio failed', error)
      });
    });
  }

  resetRefugioForm(): void {
    this.editingRefugioId = null;
    this.refugioForm.reset({
      nombre: '',
      direccion: '',
      telefono: '',
      descripcion: '',
      latitud: 2.9345,
      longitud: -75.2875,
      ciudad: 'Neiva'
    });
  }

  updateSolicitud(id: number, estado: 'APROBADA' | 'RECHAZADA'): void {
    this.adopcionService.updateEstado(id, { estado }).subscribe({
      next: () => {
        this.toastr.success(`Solicitud ${estado.toLowerCase()}`);
        this.loadDashboard();
      },
      error: (error) => console.error('[GAIA Component] Admin update solicitud failed', error)
    });
  }

  deleteSolicitud(id: number): void {
    this.openConfirm('Eliminar solicitud de adopción', 'La solicitud se borrará permanentemente.', 'Eliminar', true, () => {
      this.adopcionService.deleteSolicitud(id).subscribe({
        next: () => {
          this.toastr.success('Solicitud eliminada');
          this.loadDashboard();
        },
        error: (error) => console.error('[GAIA Component] Admin delete solicitud failed', error)
      });
    });
  }

  updateIngreso(solicitud: SolicitudIngresoMascota, estadoSolicitud: 'APROBADA' | 'RECHAZADA'): void {
    const refugioId = estadoSolicitud === 'APROBADA' ? solicitud.refugioId : undefined;
    if (estadoSolicitud === 'APROBADA' && !refugioId) {
      this.toastr.error('Crea o selecciona un refugio antes de aprobar');
      return;
    }
    this.ingresoService.updateEstado(solicitud.id, { estadoSolicitud, refugioId }).subscribe({
      next: () => {
        this.toastr.success(`Solicitud ${estadoSolicitud.toLowerCase()}`);
        this.loadDashboard();
      },
      error: (error) => console.error('[GAIA Component] Admin update ingreso failed', error)
    });
  }

  deleteIngreso(id: number): void {
    this.openConfirm('Eliminar solicitud de ingreso', 'La solicitud se borrará permanentemente.', 'Eliminar', true, () => {
      this.ingresoService.delete(id).subscribe({
        next: () => {
          this.toastr.success('Solicitud eliminada');
          this.loadDashboard();
        },
        error: (error) => console.error('[GAIA Component] Admin delete ingreso failed', error)
      });
    });
  }

  updateUserRole(user: User, role: 'ROLE_ADMIN' | 'ROLE_USER'): void {
    if (user.role === role) return;
    this.userService.updateRole(user.id, { role }).subscribe({
      next: () => {
        this.toastr.success('Rol actualizado');
        this.loadDashboard();
      },
      error: (error) => console.error('[GAIA Component] Admin update user role failed', error)
    });
  }

  deleteUser(user: User): void {
    if (user.id === this.auth.user?.id) {
      this.toastr.warning('No puedes eliminar tu propia cuenta de administrador');
      return;
    }
    if (user.role === 'ROLE_ADMIN') {
      this.toastr.warning('No puedes eliminar administradores principales');
      return;
    }
    this.openConfirm('Eliminar usuario', `Se eliminará la cuenta de ${user.nombre} ${user.apellido} y sus solicitudes asociadas.`, 'Eliminar', true, () => {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.toastr.success('Usuario eliminado');
          this.loadDashboard();
        },
        error: (error) => console.error('[GAIA Component] Admin delete user failed', error)
      });
    });
  }

  setTab(tab: AdminTab, clearSearch = true): void {
    this.activeTab = tab;
    this.page = 1;
    if (clearSearch) this.adminSearch = '';
  }

  updateSearch(value: string): void {
    this.adminSearch = value;
    this.page = 1;
  }

  goToPage(page: number): void {
    this.page = Math.min(Math.max(1, page), this.totalPages);
  }

  paged<T>(items: T[]): T[] {
    const start = (this.page - 1) * this.pageSize;
    return items.slice(start, start + this.pageSize);
  }

  openConfirm(title: string, message: string, confirmLabel: string, danger: boolean, action: () => void): void {
    this.confirmDialog = { title, message, confirmLabel, danger, action };
  }

  cancelConfirm(): void {
    this.confirmDialog = undefined;
  }

  confirmAction(): void {
    const dialog = this.confirmDialog;
    if (!dialog) return;
    this.confirmDialog = undefined;
    dialog.action();
  }

  private buildMascotaRequest(): MascotaRequest {
    const raw = this.mascotaForm.getRawValue();
    return {
      ...raw,
      fechaRescate: raw.fechaRescate || undefined,
      imagenUrl: raw.imagenUrl || undefined
    };
  }

  labelTamano(value?: string): string {
    if (value === 'PEQUENO') return 'Pequeño';
    if (value === 'GRANDE') return 'Grande';
    return 'Mediano';
  }

  labelGenero(value?: string): string {
    return value === 'HEMBRA' ? 'Hembra' : 'Macho';
  }

  private filterRows<T>(items: T[], fields: (item: T) => Array<string | number | boolean | null | undefined>): T[] {
    const search = this.adminSearch.trim().toLowerCase();
    if (!search) return items;
    return items.filter((item) => fields(item).some((value) => String(value ?? '').toLowerCase().includes(search)));
  }

  private activeRows(): unknown[] {
    if (this.activeTab === 'mascotas') return this.filteredMascotas;
    if (this.activeTab === 'refugios') return this.filteredRefugios;
    if (this.activeTab === 'solicitudes') return this.filteredSolicitudes;
    if (this.activeTab === 'ingresos') return this.filteredIngresos;
    return this.filteredUsuarios;
  }
}
