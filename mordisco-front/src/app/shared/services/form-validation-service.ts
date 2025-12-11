import { Injectable } from '@angular/core';
import { AbstractControl } from '@angular/forms';

/**
 * Servicio para manejar validaciones y mensajes de error de formularios
 */
@Injectable({
  providedIn: 'root'
})
export class FormValidationService {

  /**
   * Obtiene el mensaje de error apropiado para un campo de formulario
   * @param control - El FormControl o AbstractControl a validar
   * @param fieldName - Nombre del campo (opcional, para mensajes personalizados)
   * @returns El mensaje de error o null si no hay errores
   */
  getErrorMessage(control: AbstractControl | null, fieldName?: string): string | null {
    if (!control?.touched || !control?.errors) return null;

    const errors = control.errors;

    // Errores comunes
    if (errors['required']) {
      return '*Obligatorio';
    }

    if (errors['email']) {
      return '*Formato de email inválido';
    }

    if (errors['minlength']) {
      const requiredLength = errors['minlength'].requiredLength;
      return `*Debe tener al menos ${requiredLength} caracteres`;
    }

    if (errors['maxlength']) {
      const requiredLength = errors['maxlength'].requiredLength;
      return `*Máximo ${requiredLength} caracteres`;
    }

    if (errors['pattern']) {
      return this.getPatternErrorMessage(fieldName, control.value);
    }
      // Validación de coincidencia de contraseñas
    if (errors['passwordMismatch']) {
      return '*Las contraseñas no coinciden';
    }

    // Validaciones de hora
    if (errors['invalidHourFormat']) {
      return '*Formato inválido (máx 2 dígitos)';
    }

    if (errors['invalidHour']) {
      return '*Debe estar entre 0 y 23';
    }

    // Validaciones de minutos
    if (errors['invalidMinuteFormat']) {
      return '*Formato inválido (máx 2 dígitos)';
    }

    if (errors['invalidMinute']) {
      return '*Debe estar entre 0 y 59';
    }

    if (errors['min']) {
      return `*Valor mínimo: ${errors['min'].min}`;
    }

    if (errors['max']) {
      return `*Valor máximo: ${errors['max'].max}`;
    }

    // Error genérico
    return '*Campo inválido';
  }

  /**
   * Mensajes específicos para errores de pattern según el tipo de campo
   */
  private getPatternErrorMessage(fieldName?: string, value?: any): string {
    const lowerFieldName = fieldName?.toLowerCase();

    if (lowerFieldName?.includes('nombre') || lowerFieldName?.includes('apellido')) {
      return '*Solo se permiten letras';
    }

    if (lowerFieldName?.includes('telefono')) {
      return '*Formato válido: +54 9 223 123456';
    }

    if (lowerFieldName?.includes('password') || lowerFieldName?.includes('contraseña')) {
      return '*Debe incluir mayúscula, minúscula, número y caracter especial';
    }

    return '*Formato inválido';
  }

  /**
   * Verifica si un campo tiene errores y ha sido tocado
   */
  hasError(control: AbstractControl | null): boolean {
    return !!(control?.invalid && control?.touched);
  }

  /**
   * Obtiene clases CSS para el estado del campo
   */
  getFieldClasses(control: AbstractControl | null): { [key: string]: boolean } {
    return {
      'border-red-500': this.hasError(control),
      'focus:ring-red-500': this.hasError(control)
    };
  }
}

export function passwordMatchValidator(form: AbstractControl) {
  const password = form.get('password')?.value;
  const confirmPassword = form.get('confirmPassword')?.value;

  if (password !== confirmPassword) {
    form.get('confirmPassword')?.setErrors({ passwordMismatch: true });
  } else {
    form.get('confirmPassword')?.setErrors(null);
  }

  return null;
}