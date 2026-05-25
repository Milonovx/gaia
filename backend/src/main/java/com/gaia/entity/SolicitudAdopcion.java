package com.gaia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "solicitudes_adopcion",
        indexes = {
                @Index(name = "idx_solicitudes_adopcion_usuario", columnList = "usuario_id"),
                @Index(name = "idx_solicitudes_adopcion_mascota", columnList = "mascota_id"),
                @Index(name = "idx_solicitudes_adopcion_estado", columnList = "estado")
        }
)
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitud estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @PrePersist
    void prePersist() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoSolicitud.PENDIENTE;
        }
    }
}
