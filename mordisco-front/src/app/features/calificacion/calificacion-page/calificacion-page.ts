import { Component, inject, OnInit } from '@angular/core';
import { CalificacionComponent } from "../../../shared/components/calificacion-component/calificacion-component";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';
import { ICalificacionBase } from '../../../shared/models/calificacion/calificacion-base';
import { AuthService } from '../../../shared/services/auth-service';

@Component({
  selector: 'app-calificacion-page',
  imports: [CalificacionComponent, MatPaginator],
  templateUrl: './calificacion-page.html',
})
export class CalificacionPage implements OnInit{

  private cService = inject(CalificacionService)
  private aus = inject(AuthService)

  calificaciones? : ICalificacionBase[]
  isLoading = false
  idUsuario! : number

  // Paginación
  page = 0
  size = 10
  totalElements = 0

  ngOnInit(): void {

    this.idUsuario = this.aus.getCurrentUser()?.userId!

    this.cargarCalificaciones()
    console.log("La calificacicion", this.calificaciones)
  }

  cargarCalificaciones(){
      this.cService.getCalificacionesRepartidor(this.idUsuario,this.page, this.size).subscribe({
      next: (response) => {
        this.calificaciones = response.content;
        this.totalElements = response.totalElements;},
      error:(e)=> console.log("Errorrrr", e)
      })
  }

  onPageChange(event: PageEvent): void {
    this.page = event.pageIndex
    this.size = event.pageSize
    this.cargarCalificaciones()
  }
}
