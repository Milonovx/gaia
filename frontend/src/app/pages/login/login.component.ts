import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FaIconComponent, NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  loading = false;
  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
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
    this.auth.login(this.form.getRawValue())
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: () => {
          this.toastr.success('Sesion iniciada');
          const returnUrl = this.safeReturnUrl(this.route.snapshot.queryParamMap.get('returnUrl'));
          this.router.navigateByUrl(returnUrl);
        },
        error: (error) => console.error('[GAIA Component] Login failed', error)
      });
  }

  private safeReturnUrl(returnUrl: string | null): string {
    if (!returnUrl || !returnUrl.startsWith('/') || returnUrl.startsWith('//')) {
      return this.auth.isAdmin() ? '/admin' : '/';
    }

    return ['/login', '/registro'].includes(returnUrl.split('?')[0])
      ? (this.auth.isAdmin() ? '/admin' : '/')
      : returnUrl;
  }
}
