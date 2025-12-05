export default interface HorarioAtencionRequest {
  dia: string;
  horaApertura: string;
  horaCierre: string;
  cruzaMedianoche?: boolean;
}
