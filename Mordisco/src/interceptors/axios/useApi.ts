import { RestauranteResponse } from "../../interfaces/RestauranteResponse.model";
import axios from "axios";

const BASE_URL = "http://localhost:8080/"

export const getRestaurants = () => {
    return axios.get<RestauranteResponse[]>(`${BASE_URL}/restaurantes`);
};