import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-pago-fallido-page',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './pago-fallido-page.html'
})
export class PagoFallidoPage {
  private router = inject(Router)

  intentarNuevamente(): void {
    this.router.navigate(['/cliente/carrito'])
  }

  irAlInicio(): void {
    this.router.navigate(['/home'])
  }
}