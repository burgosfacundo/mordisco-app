import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MiMenuPage } from './mi-menu-page';

describe('MiMenuPage', () => {
  let component: MiMenuPage;
  let fixture: ComponentFixture<MiMenuPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiMenuPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MiMenuPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
