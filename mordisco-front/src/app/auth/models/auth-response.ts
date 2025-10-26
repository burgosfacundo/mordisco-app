export interface AuthResponse {
    accessToken: string;
    userId: number;
    email: string;
    role: string;
    expiresIn: number;
}
