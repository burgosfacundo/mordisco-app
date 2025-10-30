import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditPassword } from './edit-password';

describe('EditPassword', () => {
  let component: EditPassword;
  let fixture: ComponentFixture<EditPassword>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditPassword]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditPassword);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
