import { NgFor, NgIf } from '@angular/common';
import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { catchError, debounceTime, distinctUntilChanged, finalize, of } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../services/mascota.service';
import { EdadMascotaPipe } from '../../pipes/edad-mascota.pipe';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { SkeletonCardComponent } from '../../shared/skeleton-card/skeleton-card.component';

@Component({
  selector: 'app-mascotas',
  standalone: true,
  imports: [EdadMascotaPipe, EmptyStateComponent, FaIconComponent, NgFor, NgIf, ReactiveFormsModule, RouterLink, SkeletonCardComponent],
  templateUrl: './mascotas.component.html',
  styleUrl: './mascotas.component.css'
})
export class MascotasComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  loading = true;
  mascotas: Mascota[] = [];
  filteredMascotas: Mascota[] = [];
  page = 1;
  pageSize = 9;
  fallbackImage = 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=900&q=80';

  filterForm = this.fb.group({
    search: [''],
    raza: [''],
    tamano: [''],
    genero: [''],
    disponible: ['']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly mascotaService: MascotaService
  ) {}

  ngOnInit(): void {
    this.loadMascotas();
    this.filterForm.valueChanges
      .pipe(debounceTime(250), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.applyLocalSearch());
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredMascotas.length / this.pageSize));
  }

  get paginatedMascotas(): Mascota[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredMascotas.slice(start, start + this.pageSize);
  }

  loadMascotas(): void {
    this.loading = true;
    const disponibleRaw = this.filterForm.controls.disponible.value;
    this.mascotaService.getMascotas({
      raza: this.filterForm.controls.raza.value || undefined,
      tamano: this.filterForm.controls.tamano.value || undefined,
      genero: this.filterForm.controls.genero.value || undefined,
      disponible: disponibleRaw === '' ? null : disponibleRaw === 'true'
    })
      .pipe(
        catchError((error) => {
          console.error('[GAIA Component] Mascotas load failed', error);
          this.mascotas = [];
          this.filteredMascotas = [];
          return of([]);
        }),
        finalize(() => this.loading = false)
      )
      .subscribe((mascotas) => {
        this.mascotas = mascotas;
        this.applyLocalSearch();
      });
  }

  applyFilters(): void {
    this.page = 1;
    this.loadMascotas();
  }

  clearFilters(): void {
    this.filterForm.reset({
      search: '',
      raza: '',
      tamano: '',
      genero: '',
      disponible: ''
    });
    this.applyFilters();
  }

  goToPage(page: number): void {
    this.page = Math.min(Math.max(1, page), this.totalPages);
  }

  labelTamano(value?: string): string {
    if (value === 'PEQUENO') return 'Pequeño';
    if (value === 'GRANDE') return 'Grande';
    return 'Mediano';
  }

  labelGenero(value?: string): string {
    return value === 'HEMBRA' ? 'Hembra' : 'Macho';
  }

  private applyLocalSearch(): void {
    const search = (this.filterForm.controls.search.value || '').toLowerCase().trim();
    this.filteredMascotas = this.mascotas.filter((mascota) => {
      if (!search) {
        return true;
      }
      return [
        mascota.nombre,
        mascota.raza,
        mascota.genero,
        mascota.tamano,
        mascota.estadoSalud,
        mascota.refugio?.nombre
      ].some((value) => value?.toLowerCase().includes(search));
    });
    this.page = Math.min(this.page, this.totalPages);
  }
}
