import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [AsyncPipe, FaIconComponent, NgIf, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  currentUser$ = this.auth.currentUser$;
  isAuthenticated$ = this.auth.isAuthenticated$;
  isAdmin$ = this.auth.isAdmin$;

  constructor(public readonly auth: AuthService) {}

  logout(): void {
    this.auth.logout();
  }
}
