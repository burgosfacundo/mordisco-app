import { Component, inject, input, OnInit } from '@angular/core';
import RestauranteForCard from '../../models/restaurante/restaurante-for-card';
import HorarioAtencion from '../../models/horario/horario-atencion-request';
import { Router } from '@angular/router';
import { HorarioService } from '../../services/horario/horario-service';
import HorarioAtencionResponse from '../../models/horario/horario-atencion-response';


@Component({
  selector: 'app-restaurante-card-component',
  imports: [],
  templateUrl: './restaurante-card-component.html'
})
export class RestauranteCardComponent implements OnInit {
  restaurante = input<RestauranteForCard>()
  listaHorarios : HorarioAtencionResponse [] = []
  private haService = inject(HorarioService)
  private router : Router = inject(Router)

  ngOnInit(): void {
    this.getHorariosByRestaurante();
  }

  getHorarios(): string {
    const h = this.getHorarioDeHoy();

    if (!h) return 'Cerrado';

    return this.isOpenNow(h)
      ? `${this.formatHHmm(h.horaApertura)} - ${this.formatHHmm(h.horaCierre)}`
      : 'Cerrado';
  }

  isAbierto(){
    return this.getHorarios() === 'Cerrado' ? false : true
  }

  getHorariosByRestaurante(){
    if(this.restaurante()?.id){
      this.haService.getAllByRestauranteId(this.restaurante()?.id!).subscribe({
      next : (data) => this.listaHorarios=data,
      error : (e)=>{
        console.log("No se ha podido listar los horarios", e)
      }
      })
    }
  }

  private getHorarioDeHoy(): HorarioAtencion | undefined {
    const dias = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
    const dia = dias[new Date().getDay()];
    return this.listaHorarios.find(h => h.dia === dia);
  }

  private nowInMinutes(): number {
    const now = new Date();
    return now.getHours() * 60 + now.getMinutes();
  }

  private toMinutes(time: string): number {
    const [h, m, s] = time.split(':').map(Number);
    return (h || 0) * 60 + (m || 0) + Math.floor((s || 0) / 60);
  }

  private isOpenNow(h: HorarioAtencion): boolean {
    const open = this.toMinutes(h.horaApertura);
    const close = this.toMinutes(h.horaCierre);
    const now = this.nowInMinutes();

    if (open === close) return false;

    if (close > open) return now >= open && now < close;


    return now >= open || now < close;
  }

  private formatHHmm(time: string): string {
    const [h, m] = time.split(':');
    return `${h.padStart(2, '0')}:${(m ?? '00').padStart(2, '0')}`;
  }

  verMenu(r : RestauranteForCard){
    this.router.navigate(['/cliente/restaurante', r.id])
  }
}
