import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function mustBeEntity<T extends { id: string }>(errorKey = 'mustBeEntity'): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const v = control.value;
    if (v == null || v === '') return null;
    return typeof v === 'object' && v?.id ? null : { [errorKey]: true };
  };
}
