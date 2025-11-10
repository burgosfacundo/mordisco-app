import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
})
export class Footer {
  currentYear: number = new Date().getFullYear();

}
