export default interface UserProfile{
    id : number,
    nombre : string,
    apellido : string,
    telefono : string,
    email : string,
    rol : RolDTO
    bajaLogica : boolean,
    motivoBaja : string,
    fechaBaja : string
}

interface RolDTO {
  id: number;
  nombre: string;
}