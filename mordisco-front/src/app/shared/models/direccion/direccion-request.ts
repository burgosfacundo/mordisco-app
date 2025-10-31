export default interface DireccionRequest{
    calle : string,
    numero : string,
    piso?: string,
    depto?: string,
    codigoPostal: string,
    referencias? : string,
    latitud?: number,
    longitud? : number,
    ciudad: string
}