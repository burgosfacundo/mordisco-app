import { Component, input } from '@angular/core';
import DireccionResponse from '../../models/direccion/direccion-response';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-direccion-card-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './direccion-card-component.html'
})
export class DireccionCardComponent {
  direccion = input<DireccionResponse>();
}