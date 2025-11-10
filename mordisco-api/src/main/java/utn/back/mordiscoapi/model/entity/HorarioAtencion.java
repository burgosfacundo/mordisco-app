package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity @Table(name = "horarios_atencion")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class HorarioAtencion {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dia;

    @Column (nullable = false)
    private LocalTime horaApertura;

    @Column (nullable = false)
    private LocalTime horaCierre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;
}

