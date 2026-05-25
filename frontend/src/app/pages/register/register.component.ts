import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FaIconComponent, NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  loading = false;
  form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(80)]],
    apellido: ['', [Validators.required, Validators.maxLength(80)]],
    email: ['', [Validators.required, Validators.email]],
    telefono: ['', [Validators.maxLength(30)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(120)]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
    private readonly toastr: ToastrService
  ) {}

  ngOnInit(): void {
    if (this.auth.isAuthenticated()) {
      this.router.navigateByUrl(this.auth.isAdmin() ? '/admin' : '/perfil');
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.auth.register(this.form.getRawValue())
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: () => {
          this.toastr.success('Cuenta creada');
          this.router.navigate(['/mascotas']);
        },
        error: (error) => console.error('[GAIA Component] Register failed', error)
      });
  }
}
