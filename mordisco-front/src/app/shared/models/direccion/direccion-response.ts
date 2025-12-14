export default interface DireccionResponse{
    id : number,
    calle : string,
    numero : string,
    piso?: string,
    depto?: string,
    codigoPostal: string,
    referencias? : string,
    alias : string,
    latitud: number,
    longitud : number,
    ciudad: string
}