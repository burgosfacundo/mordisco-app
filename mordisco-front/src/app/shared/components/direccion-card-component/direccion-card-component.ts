import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import DireccionResponse from '../../models/direccion/direccion-response';

@Component({
  selector: 'app-direccion-card-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './direccion-card-component.html'
})
export class DireccionCardComponent {
  @Input() direccion!: DireccionResponse;
}