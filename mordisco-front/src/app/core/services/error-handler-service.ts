import { Injectable, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';

export type ErrorSeverity = 'error' | 'warning' | 'info' | 'success';

export interface ErrorContext {
  type: 'http' | 'validation' | 'business' | 'network';
  severity: ErrorSeverity;
  userMessage: string;
  technicalMessage: string;
  showToUser: boolean;
  statusCode?: number;
  action?: {
    label: string;
    callback: () => void;
  };
}

@Injectable({ providedIn: 'root' })
export class ErrorHandlerService {
  private router = inject(Router);

  /**
   * Mapeo de códigos HTTP a mensajes user-friendly
   */
  private readonly HTTP_ERROR_MESSAGES: Record<number, string> = {
    400: 'Los datos enviados no son válidos. Por favor, verifica la información.',
    401: 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.',
    403: 'No tienes permisos para realizar esta acción.',
    404: 'El recurso solicitado no fue encontrado.',
    409: 'Ya existe un registro con estos datos.',
    422: 'Los datos no cumplen con los requisitos necesarios.',
    500: 'Ocurrió un error en el servidor. Por favor, intenta nuevamente.',
    502: 'El servidor no está respondiendo correctamente.',
    503: 'El servicio no está disponible en este momento. Intenta más tarde.',
    504: 'El servidor tardó demasiado en responder. Intenta nuevamente.'
  };

  /**
   * Maneja errores HTTP y los convierte en un contexto de error
   */
  handleHttpError(error: HttpErrorResponse, customMessage?: string): ErrorContext {
    const statusCode = error.status;
    
    // Error de red (sin conexión)
    if (statusCode === 0) {
      return {
        type: 'network',
        severity: 'error',
        userMessage: customMessage || 'No se pudo conectar con el servidor. Verifica tu conexión a internet.',
        technicalMessage: error.message,
        showToUser: true,
        statusCode: 0
      };
    }

    // Error 401 - Sesión expirada
    // Solo redirigir si NO estamos en la página de login
    if (statusCode === 401 && !this.router.url.includes('/login')) {
      setTimeout(() => {
        localStorage.clear();
        this.router.navigate(['/login']);
      }, 2000);
    }

    // Obtener mensaje del backend si existe
    const backendMessage = this.extractBackendMessage(error);
    const defaultMessage = this.HTTP_ERROR_MESSAGES[statusCode] || 'Ocurrió un error inesperado.';
    
    // Para errores 500+ (servidor), NO mostrar mensaje del backend (puede ser técnico/sensible)
    // Para errores 4xx (cliente), SÍ mostrar mensaje del backend (validación/negocio)
    const shouldUseBackendMessage = statusCode < 500;
    
    return {
      type: 'http',
      severity: this.getSeverityByStatus(statusCode),
      userMessage: customMessage || (shouldUseBackendMessage ? backendMessage : null) || defaultMessage,
      technicalMessage: `HTTP ${statusCode}: ${error.message}`,
      showToUser: true,
      statusCode
    };
  }

  /**
   * Maneja errores de validación de formularios
   */
  handleValidationError(message: string): ErrorContext {
    return {
      type: 'validation',
      severity: 'warning',
      userMessage: message,
      technicalMessage: message,
      showToUser: true
    };
  }

  /**
   * Maneja errores de lógica de negocio
   */
  handleBusinessError(message: string, severity: ErrorSeverity = 'warning'): ErrorContext {
    return {
      type: 'business',
      severity,
      userMessage: message,
      technicalMessage: message,
      showToUser: true
    };
  }

  /**
   * Procesa el error y retorna un Observable para usar en catchError
   */
  handle(error: any, customMessage?: string): Observable<never> {
    let errorContext: ErrorContext;

    if (error instanceof HttpErrorResponse) {
      errorContext = this.handleHttpError(error, customMessage);
    } else if (error instanceof Error) {
      errorContext = {
        type: 'business',
        severity: 'error',
        userMessage: customMessage || 'Ocurrió un error inesperado.',
        technicalMessage: error.message,
        showToUser: true
      };
    } else {
      errorContext = {
        type: 'business',
        severity: 'error',
        userMessage: customMessage || 'Ocurrió un error inesperado.',
        technicalMessage: String(error),
        showToUser: true
      };
    }

    // Log para debugging (en producción usar servicio de logging)
    this.logError(errorContext);

    return throwError(() => errorContext);
  }

  /**
   * Extrae mensaje de error del backend si existe
   */
  private extractBackendMessage(error: HttpErrorResponse): string | null {
    if (!error.error) {
      return null;
    }

    // Si error.error es un string, intentar parsearlo como JSON
    if (typeof error.error === 'string') {
      try {
        const parsed = JSON.parse(error.error);
        return this.extractMessageFromObject(parsed);
      } catch {
        // Si no es JSON válido, retornar el string tal cual
        return error.error;
      }
    }

    // Si error.error es un objeto, extraer el mensaje
    if (typeof error.error === 'object') {
      return this.extractMessageFromObject(error.error);
    }

    return null;
  }

  /**
   * Extrae el mensaje de un objeto de error
   * Maneja diferentes formatos del backend:
   * - { message: "..." }
   * - { error: "..." }
   * - { fieldName: "mensaje de validación" } (ej: { password: "La contraseña debe..." })
   */
  private extractMessageFromObject(obj: any): string | null {
    if (!obj) {
      return null;
    }

    // Priorizar campos estándar
    if (obj.message) {
      return obj.message;
    }
    if (obj.error) {
      return obj.error;
    }
    if (obj.mensaje) {
      return obj.mensaje;
    }

    // Si no tiene campos estándar, extraer el primer valor del objeto
    // Esto maneja casos de validación de DTO: { password: "mensaje", email: "mensaje" }
    const values = Object.values(obj);
    if (values.length > 0 && typeof values[0] === 'string') {
      return values[0];
    }

    return null;
  }

  /**
   * Determina la severidad según el código de estado HTTP
   */
  private getSeverityByStatus(status: number): ErrorSeverity {
    if (status >= 500) return 'error';
    if (status >= 400) return 'warning';
    return 'info';
  }

  /**
   * Log estructurado de errores
   */
  private logError(context: ErrorContext): void {
    const logData = {
      timestamp: new Date().toISOString(),
      type: context.type,
      severity: context.severity,
      userMessage: context.userMessage,
      technicalMessage: context.technicalMessage,
      statusCode: context.statusCode,
      url: window.location.href
    };

    if (context.severity === 'error') {
      console.error('[ErrorHandler]', logData);
    } else if (context.severity === 'warning') {
      console.warn('[ErrorHandler]', logData);
    } else {
      console.info('[ErrorHandler]', logData);
    }
  }
}
