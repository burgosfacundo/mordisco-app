import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntregasPage } from './entregas-page';

describe('EntregasPage', () => {
  let component: EntregasPage;
  let fixture: ComponentFixture<EntregasPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntregasPage]
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
