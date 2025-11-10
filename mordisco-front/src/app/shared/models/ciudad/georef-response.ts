import { LocalidadCensal } from "./localidad-censal";

export interface GeoRefLocalidadesResponse {
  cantidad: number;
  inicio: number;
  total: number;
  localidades_censales: LocalidadCensal[];
}