import { Component, inject, ViewChild } from '@angular/core';
import { UserService } from '../../registro/services/user-service';
import UserCard from '../../../shared/models/user/user-card';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { UsuarioCardComponent } from "../../../shared/components/usuario-card-component/usuario-card-component";
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { BarraBuscadoraComponent } from '../../../shared/components/barra-buscadora-component/barra-buscadora-component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-usuarios-page',
  imports: [UsuarioCardComponent, MatPaginator, BarraBuscadoraComponent, FormsModule],
  templateUrl: './usuarios-page.html',
})
export class UsuariosPage {
  private usuarioService = inject(UserService);
  protected usuarios : UserCard[] = [];

  private searchSubject = new Subject<string>();
  @ViewChild(BarraBuscadoraComponent) barraBuscadora!: BarraBuscadoraComponent;

  filtroRol: string = '';
  filtroActividad: string = '';
  searchValue: string = '';

  sizeUsuarios : number = 12;
  pageUsuarios : number = 0;
  lengthUsuarios : number = 12;

  isLoadingUsuarios = true;

  ngOnInit(): void {
    this.loadUsuarios()
    this.searchSubject.pipe(
      debounceTime(500), // Espera 500ms después de que el usuario deje de escribir
      distinctUntilChanged() // Solo emite si el valor cambió
    ).subscribe(() => {
      this.pageUsuarios = 0; // Resetear a la primera página
      this.buscar();
    });     
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

    if (this.searchValue.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.buscar();
    } else {
      this.loadUsuarios();
    }  
  }

  aplicarFiltros(): void {
    this.pageUsuarios = 0;
    this.buscar();
  }

  private tieneFiltrosAplicados(): boolean {
    return this.filtroRol !== '' ||
           this.filtroActividad !== ''
  }

buscar() {
  this.usuarioService.filtrarUsuarios(
    this.searchValue,
    this.filtroActividad,  
    this.filtroRol,        
    this.pageUsuarios, 
    this.sizeUsuarios
  ).subscribe({
    next: (resp) => {
      this.usuarios = resp.content;
      this.lengthUsuarios = resp.totalElements;
      this.isLoadingUsuarios = false;
    },
    error: () => {
      this.isLoadingUsuarios = false;
    }
  });
}


 onSearchChanged(text: string) {
    this.searchValue = text;
    
    // Si hay texto o hay filtros aplicados, buscar
    if (text.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.searchSubject.next(text);
    } else if (text.trim() === '' && !this.tieneFiltrosAplicados()) {
      // Si borraron todo y no hay filtros, cargar todos
      this.pageUsuarios = 0;
      this.loadUsuarios();
    }
  }


  onClearFilters(): void {
    this.searchValue = '';
    this.filtroRol = '';
    this.filtroActividad = '';
    this.pageUsuarios = 0;
    
    // Limpiar la barra buscadora visualmente
    if (this.barraBuscadora) {
      this.barraBuscadora.onSearchClear();
    }
    
    this.loadUsuarios();
  }  

}
