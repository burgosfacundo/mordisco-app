export interface JwtUser {
    id: number
    email: string
    role: {
      id: number
      nombre: string
    }
  }