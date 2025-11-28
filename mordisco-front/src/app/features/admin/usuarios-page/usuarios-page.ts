import { Component, inject } from '@angular/core';
import { UserService } from '../../registro/services/user-service';
import UserCard from '../../../shared/models/user/user-card';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { UsuarioCardComponent } from "../../../shared/components/usuario-card-component/usuario-card-component";

@Component({
  selector: 'app-usuarios-page',
  imports: [UsuarioCardComponent, MatPaginator],
  templateUrl: './usuarios-page.html',
})
export class UsuariosPage {
  private usuarioService = inject(UserService);
  protected usuarios : UserCard[] = [];

  sizeUsuarios : number = 12;
  pageUsuarios : number = 0;
  lengthUsuarios : number = 12;

  isLoadingUsuarios = true;

  ngOnInit(): void {
    this.loadUsuarios()
  }

  loadUsuarios(): void {
    this.usuarioService.getAll(this.pageUsuarios,this.sizeUsuarios).subscribe({
      next: (data) => {
        console.log(data.content);
        
        this.usuarios = data.content;
        this.lengthUsuarios = data.totalElements
        this.isLoadingUsuarios = false;
      },
      error: () => {
        this.isLoadingUsuarios = false;
      }
    });
  }

  onPageChangeUsuarios(event: PageEvent): void {
    this.pageUsuarios = event.pageIndex
    this.sizeUsuarios = event.pageSize;
    this.loadUsuarios();
  }

}
