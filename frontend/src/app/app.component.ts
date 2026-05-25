import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import {
  faArrowRight,
  faBars,
  faBell,
  faChartLine,
  faCheck,
  faCircleInfo,
  faClock,
  faDog,
  faEdit,
  faEnvelope,
  faEye,
  faHeart,
  faHouse,
  faLocationDot,
  faLock,
  faMapLocationDot,
  faPaw,
  faPenToSquare,
  faPlus,
  faSearch,
  faShieldHeart,
  faSignOutAlt,
  faSpinner,
  faTrash,
  faUser,
  faUserPlus,
  faXmark
} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: '<router-outlet></router-outlet>'
})
export class AppComponent {
  constructor(library: FaIconLibrary) {
    library.addIcons(
      faArrowRight,
      faBars,
      faBell,
      faChartLine,
      faCheck,
      faCircleInfo,
      faClock,
      faDog,
      faEdit,
      faEnvelope,
      faEye,
      faHeart,
      faHouse,
      faLocationDot,
      faLock,
      faMapLocationDot,
      faPaw,
      faPenToSquare,
      faPlus,
      faSearch,
      faShieldHeart,
      faSignOutAlt,
      faSpinner,
      faTrash,
      faUser,
      faUserPlus,
      faXmark
    );
  }
}
