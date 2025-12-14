export interface AuthResponse {
    accessToken: string;
    userId: number;
    email: string;
    nombre: string;
    role: string;
    expiresIn: number;
}
