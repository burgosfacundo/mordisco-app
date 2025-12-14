export default interface DireccionRequest{
    calle : string,
    numero : string,
    piso?: string,
    depto?: string,
    codigoPostal: string,
    alias? : string,
    referencias? : string,
    ciudad: string
}