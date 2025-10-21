export interface JwtClaims {
    role: {
      id: number
      nombre: string
    };
    id: number
    email: string
    iat: number
    exp: number
  }