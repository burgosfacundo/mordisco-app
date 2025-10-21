import Address from "./address";

export default interface User{
    nombre : string,
    apellido : string,
    telefono : string,
    email : string,
    password : string,
    rolId : number,
    direcciones: Address[];
}
  