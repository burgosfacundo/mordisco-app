import { Component, output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-barra-buscadora-component',
  imports: [ReactiveFormsModule],
  templateUrl: './barra-buscadora-component.html',
})
export class BarraBuscadoraComponent {

  searchChanged = output<string>();
  searchTerm = new FormControl('');

  constructor() {
    // Emitimos cada vez que cambia el valor
    this.searchTerm.valueChanges.subscribe(value => {
      this.searchChanged.emit(value ?? '');
    });
  }

  onSearchClear(): void {
    this.searchTerm.setValue('');
  }
  
}
