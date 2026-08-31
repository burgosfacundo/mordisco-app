import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ConfiguracionPage } from './configuracion-page';

describe('ConfiguracionPage', () => {
  let component: ConfiguracionPage;
  let fixture: ComponentFixture<ConfiguracionPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfiguracionPage],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfiguracionPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
