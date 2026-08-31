import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { EntregasPage } from './entregas-page';

describe('EntregasPage', () => {
  let component: EntregasPage;
  let fixture: ComponentFixture<EntregasPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntregasPage],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntregasPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
