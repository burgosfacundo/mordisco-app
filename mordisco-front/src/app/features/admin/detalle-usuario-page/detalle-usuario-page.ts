import { Component, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../registro/services/user-service';
import UserProfile from '../../../shared/models/user/user-profile';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { MisPedidosClientePage } from '../../mis-pedidos/components/mis-pedidos-cliente-page/mis-pedidos-cliente-page';
import { MatIcon } from "@angular/material/icon";
import { EntregasPage } from '../../entregas/entregas-page/entregas-page';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import CalificacionPedidoResponseDTO from '../../../shared/models/calificacion/calificacion-pedido-response-dto';
import { CalificacionComponent } from '../../../shared/components/calificacion-component/calificacion-component';
import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';
import CalificacionRepartidorResponseDTO from '../../../shared/models/calificacion/calificacion-repartidor-response-dto';
import { ICalificacionBase } from '../../../shared/models/calificacion/ICalificacionBase';
import { CalificacionCardAdmin } from "../../../shared/components/calificacion-card-admin/calificacion-card-admin";
import RestauranteResponse from '../../../shared/models/restaurante/restaurante-response';
import { RestauranteService } from '../../../shared/services/restaurante/restaurante-service';

@Component({
  selector: 'app-detalle-usuario-page',
  imports: [MisPedidosClientePage, EntregasPage, MatIcon, MatPaginator, CalificacionCardAdmin],
  templateUrl: './detalle-usuario-page.html',
})
export class DetalleUsuarioPage {

  private _snackBar = inject(MatSnackBar);
  private uService = inject(UserService);
  private rService = inject (RestauranteService)
  private cService = inject(CalificacionService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  protected user? : UserProfile
  protected rol? : string | null
  protected restaurante? : RestauranteResponse
  calificacionesPedidos?: CalificacionPedidoResponseDTO[];
  calificacionesRepartidores? : CalificacionRepartidorResponseDTO[];
  clienteFlag = false
  protected isLoading = true;

  sizeCalificacionPedidos: number = 5;
  pageCalificacionPedidos: number = 0;
  lengthCalificacionPedidos: number = 0;

  sizeCalificacionRepartidores: number = 5;
  pageCalificacionRepartidores: number = 0;
  lengthCalificacionRepartidores: number = 0;


  ngOnInit(): void {
    this.loadUsuario();
  }

  private loadUsuario(): void {
    const id = this.route.snapshot.paramMap.get('idUser');
    this.rol = this.route.snapshot.paramMap.get('idRol');
    
    if (!id) {
      this._snackBar.open('❌ ID de pedido no válido', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/restaurante/pedidos']);
      return;
    }
    
    this.uService.getUserByID(Number(id)).subscribe({
      next: (data) => {
        this.user = data;
        this.isLoading = false

        if(this.user.id){
          this.setFlag();
          this.buscarRestaurante()
          this.cargarCalificacionesPedidos(this.rol!);
          this.cargarCalificacionesRepartidor(this.rol!);
        }
      },
      error: (error) => {
        console.error('Error al cargar usuario:', error);
        this._snackBar.open('❌ Error al cargar el usuario', 'Cerrar', { duration: 3000 });
        this.isLoading = false;
        this.router.navigate(['/']);
      }
    });

  }

  private cargarCalificacionesPedidos(rol : string): void {
    if (rol === 'ROLE_RESTAURANTE'){
      if(this.restaurante?.id){
      }
      this.cService.getCalificacionesRestaurante(this.restaurante?.id!,this.pageCalificacionPedidos, this.sizeCalificacionPedidos).subscribe({
      next: (response) => {
        this.calificacionesPedidos = response.content;
        this.lengthCalificacionPedidos = response.totalElements;
        console.log("Printea las calificaciones ", response.content)
      },error: (e)=> console.log(e)
    });      
    }else if(rol === 'ROLE_CLIENTE'){
      this.cService.getCalificacionesPedidosCliente(this.pageCalificacionRepartidores,this.sizeCalificacionRepartidores,this.user?.id!).subscribe({
        next:(response)=>{
          this.calificacionesPedidos = response.content;
          this.lengthCalificacionPedidos = response.totalElements;
          console.log("CALIFICACIONES PPPPP",this.calificacionesPedidos)

        }
      })
    }
  }

  cargarCalificacionesRepartidor(rol : string){
    if(rol === 'ROLE_REPARTIDOR'){
      this.cService.getCalificacionesRepartidor(this.user?.id!,this.pageCalificacionRepartidores, this.sizeCalificacionRepartidores).subscribe({
      next: (response) => {
        this.calificacionesRepartidores = response.content;
        this.lengthCalificacionRepartidores = response.totalElements;
                  console.log("CALIFICACIONES RRRRR",this.calificacionesRepartidores)

      }});
    }else if(rol === 'ROLE_CLIENTE'){
      this.cService.getCalificacionesRepartidorCliente(this.pageCalificacionRepartidores,this.sizeCalificacionRepartidores, this.user?.id!).subscribe({
          next:(response)=>{
          this.calificacionesRepartidores = response.content;
          this.lengthCalificacionRepartidores = response.totalElements;
        }
      })
    }
  }

  onPageChangeCalificacionPedido(event: PageEvent, rol : string): void {
    this.pageCalificacionPedidos = event.pageIndex;
    this.sizeCalificacionPedidos = event.pageSize;
    this.cargarCalificacionesPedidos(rol);
  }

  onPageChangeCalificacionRepartidor(event: PageEvent, rol : string): void {
    this.pageCalificacionRepartidores = event.pageIndex;
    this.sizeCalificacionRepartidores = event.pageSize;
    this.cargarCalificacionesRepartidor(rol);
  }

  setFlag(){
    if(this.rol === 'ROLE_CLIENTE'){
      this.clienteFlag = true
    }
  }

  buscarRestaurante(){
    if(!this.user?.id) return
    this.rService.getByUsuario(this.user?.id).subscribe({
      next:(r)=> {this.restaurante= r
        this.cargarCalificacionesPedidos(this.rol!);
      }})
  }

  volver(): void {
    this.router.navigate(['/restaurante/pedidos']);
  }
  
}