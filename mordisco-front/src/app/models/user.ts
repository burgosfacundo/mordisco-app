import Address from "./address";

export default interface User{
    id: number, ///////BORRAR SI ME OLVIDO EN EL MERGE
    nombre : string,
    apellido : string,
    telefono? : string,
    email? : string,
    password? : string,
    rolId? : number,
    direcciones?: Address[];
    //Agrego los signos de pregunta paa que cuando un pedido devuelva un cliente que solo tiene id, nombre y apellido, 
    //lo pueda guardar aca y no tener que crear un UsuarioPedidoResponse (Segun chan gpt en este contexto es lo mejor)
}
  