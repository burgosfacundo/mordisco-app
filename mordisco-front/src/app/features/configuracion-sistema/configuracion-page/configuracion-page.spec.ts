import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfiguracionPage } from './configuracion-page';

describe('ConfiguracionPage', () => {
  let component: ConfiguracionPage;
  let fixture: ComponentFixture<ConfiguracionPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfiguracionPage]
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
