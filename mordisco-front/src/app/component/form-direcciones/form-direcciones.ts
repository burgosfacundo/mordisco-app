import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ControlContainer, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-form-direcciones',
  imports: [ReactiveFormsModule, CommonModule],
  viewProviders: [{ provide: ControlContainer, useExisting: FormGroupDirective }],
  templateUrl: './form-direcciones.html',
  styleUrl: './form-direcciones.css'
})
export class FormDirecciones {
    private parentFormDirective = inject(FormGroupDirective);
}