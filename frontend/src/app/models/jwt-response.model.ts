import { User } from './user.model';

export interface JwtResponse {
  token: string;
  tokenType: string;
  usuario: User;
}
