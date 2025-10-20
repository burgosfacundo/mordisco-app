import Direccion from "./Direccion";

export default interface Usuario{
    nombre : string,
    apellido : string,
    telefono : string,
    email : string,
    password : string,
    rol : number,
    direccion: Direccion;
}
  