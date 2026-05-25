import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const appRoutes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/home/home.component').then((m) => m.HomeComponent)
      },
      {
        path: 'login',
        loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent)
      },
      {
        path: 'registro',
        loadComponent: () => import('./pages/register/register.component').then((m) => m.RegisterComponent)
      },
      {
        path: 'mascotas',
        loadComponent: () => import('./pages/mascotas/mascotas.component').then((m) => m.MascotasComponent)
      },
      {
        path: 'mascotas/:id',
        loadComponent: () => import('./pages/mascota-detalle/mascota-detalle.component').then((m) => m.MascotaDetalleComponent)
      },
      {
        path: 'refugios',
        loadComponent: () => import('./pages/refugios/refugios.component').then((m) => m.RefugiosComponent)
      },
      {
        path: 'solicitudes',
        canActivate: [authGuard],
        loadComponent: () => import('./pages/solicitudes/solicitudes.component').then((m) => m.SolicitudesComponent)
      },
      {
        path: 'entregar-mascota',
        canActivate: [authGuard],
        loadComponent: () => import('./pages/entregar-mascota/entregar-mascota.component').then((m) => m.EntregarMascotaComponent)
      },
      {
        path: 'perfil',
        canActivate: [authGuard],
        loadComponent: () => import('./pages/perfil-usuario/perfil-usuario.component').then((m) => m.PerfilUsuarioComponent)
      },
      {
        path: 'admin',
        canActivate: [adminGuard],
        loadComponent: () => import('./pages/dashboard-admin/dashboard-admin.component').then((m) => m.DashboardAdminComponent)
      },
      {
        path: '**',
        loadComponent: () => import('./pages/not-found/not-found.component').then((m) => m.NotFoundComponent)
      }
    ]
  }
];
