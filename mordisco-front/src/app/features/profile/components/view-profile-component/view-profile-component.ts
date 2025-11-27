import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../../registro/services/user-service';
import UserProfile from '../../../../shared/models/user/user-profile';
import { AuthService } from '../../../../shared/services/auth-service';
import { NotificationService } from '../../../../core/services/notification-service';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';

@Component({
  selector: 'app-view-profile',
  imports: [],
  templateUrl: './view-profile-component.html',
})
export class ViewProfileComponent {
  private authService = inject(AuthService)
  private userService : UserService = inject(UserService)
  private router : Router = inject(Router)
  private notificationService = inject(NotificationService)
  private dialog = inject(MatDialog)
  usuario? : UserProfile


  ngOnInit(): void {
    this.userService.getMe().subscribe({
      next: u => this.usuario = u,
      error: () => {
        this.router.navigate(['/'])
      }
    })
  }

 confirmarEliminacion(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { mensaje: '¿Estás segura/o de que querés eliminar tu cuenta? Esta acción no se puede deshacer.' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.eliminarCuenta();
      }
    });
  }
  
  eliminarCuenta(): void {
    this.userService.deleteMe().subscribe({
      next: () => {
        this.notificationService.success('Cuenta eliminada correctamente');
        this.authService.logout();
      }
    });
}

  editarPerfil(){
    this.router.navigate(['/profile/edit'])
  }
}
