import { RestauranteResponse } from "../../interfaces/RestauranteResponse.model";
import { MenuResponse } from "../../interfaces/MenuResponse.model";
import { UseApiCall } from "../../interfaces/useApi.model";
import { getAxios } from '../axios.interceptor';
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

    const call = axios.get<RestauranteResponse[]>(`${BASE_URL}/restaurante`, {
        signal: controller.signal,
    });

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

    const call = axios.post<UserLoginResponse>(
        `${BASE_URL}/usuario/login`,
        { email, password },
        { signal: controller.signal }
    );

    return { call, controller };
};

export const signUp = ({ nombre, apellido, telefono, email, password, rolId }: SignUpParams): UseApiCall<string> => {
    const controller = new AbortController();
    const axios = getAxios();

    const call = axios.post<string>(
        `${BASE_URL}/usuario/save`,
        { nombre, apellido, telefono, email, password, rolId },
        { signal: controller.signal }
    );
    return { call, controller };
};


