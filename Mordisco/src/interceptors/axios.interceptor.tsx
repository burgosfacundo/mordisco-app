import axios, {
  AxiosInstance,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";

let axiosInstance: AxiosInstance;

export const initAxios = (baseURL: string = "http://localhost:8080/api") => {
  axiosInstance = axios.create({
    baseURL,
  });

  axiosInstance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = localStorage.getItem("token");
      if (token) {
        config.headers = config.headers ?? {};
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      console.log(`Request made to: ${config.url}`);
      return config;
    },
    (error) => Promise.reject(error)
  );

  axiosInstance.interceptors.response.use(
    (response: AxiosResponse) => {
      console.log(`Response from: ${response.config.url}`, {
        data: response.data,
        status: response.status,
      });
      return response;
    },
    (error) => {
      if (error.response) {
        console.error(`Error response from: ${error.response.config.url}`, error.response.data);
      } else {
        console.error(`Error: ${error.message}`);
      }
      return Promise.reject(error);
    }
  );
};

export const getAxios = (): AxiosInstance => {
  if (!axiosInstance) {
    throw new Error("Axios instance not initialized. Call initAxios(baseURL) first.");
  }
  return axiosInstance;
};
