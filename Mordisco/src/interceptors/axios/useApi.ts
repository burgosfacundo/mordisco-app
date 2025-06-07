import { RestauranteResponse } from "../../interfaces/RestauranteResponse.model";
import { MenuResponse } from "../../interfaces/MenuResponse.model";
import { UseApiCall } from "../../interfaces/useApi.model";
import { getAxios } from '../axios.interceptor';
import { UserResponse } from "../../interfaces/UserResponse.model";
import { PedidoRequest } from "../../interfaces/PedidoRequest.model";
interface UserLoginResponse {
    jwt: string
}

interface LoginParams{
    email: string,
    password: string
}

interface SignUpParams{
    nombre:string,
    apellido:string,
    telefono:string,
    email: string,
    password: string,
    rolId:number
}

const BASE_URL = "http://localhost:8080/api"

export const fetchRestaurants = (): UseApiCall<RestauranteResponse[]> => {
    const controller = new AbortController();
    const axios = getAxios();

    const call = axios.get<RestauranteResponse[]>(
        `${BASE_URL}/restaurante/estado?estado=true`,
        { signal: controller.signal }
    );

    return { call, controller };
};

export const fetchMenu = (id: number): UseApiCall<MenuResponse> => {
    const controller = new AbortController();
    const axios = getAxios();

    const call = axios.get<MenuResponse>(`${BASE_URL}/menu/${id}`, {
        signal: controller.signal,
    });
    
    return { call, controller };
};

export const login = ({ email, password }: LoginParams): UseApiCall<UserLoginResponse> => {
    const controller = new AbortController();
    const axios = getAxios();
    console.log("")
    const call = axios.post<UserLoginResponse>(
        `${BASE_URL}/auth/login`,
        { email, password },
        { signal: controller.signal }
    );

    return { call, controller };
};

export const signUp = ({ nombre, apellido, telefono, email, password, rolId }: SignUpParams): UseApiCall<string> => {
    const controller = new AbortController();
    const axios = getAxios();
    console.log("")
    const call = axios.post<string>(
        `${BASE_URL}/usuario/save`,
        { nombre, apellido, telefono, email, password, rolId },
        { signal: controller.signal }
    );
    return { call, controller };
};

export const fetchRestaurantsByPromocion = (): UseApiCall<RestauranteResponse[]> => {
    const controller = new AbortController();
    const axios = getAxios();

    const call = axios.get<RestauranteResponse[]>(`${BASE_URL}/restaurante/promocion`, {
        signal: controller.signal,
    });

    return { call, controller };
};

export const fetchUserById = (id:number): UseApiCall<UserResponse> =>{
    const controller = new AbortController();
    const axios = getAxios();

    const call = axios.get<UserResponse>(`${BASE_URL}/usuario/${id}`, {
        signal: controller.signal,
    });
    
    return { call, controller };
}

export const sendPedido = (pedido: PedidoRequest): UseApiCall<string> => {
    const controller = new AbortController();
    const axios = getAxios();

    const call = axios.post(`${BASE_URL}/pedido/save`, pedido, {
        signal: controller.signal
    });

    return { call, controller };
};



