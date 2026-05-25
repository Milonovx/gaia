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
        name = "solicitudes_ingreso_mascota",
        indexes = {
                @Index(name = "idx_solicitudes_ingreso_usuario", columnList = "usuario_id"),
                @Index(name = "idx_solicitudes_ingreso_refugio", columnList = "refugio_id"),
                @Index(name = "idx_solicitudes_ingreso_estado", columnList = "estado_solicitud")
        }
)
public class SolicitudIngresoMascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "refugio_id", nullable = false)
    private Refugio refugio;

    @Column(nullable = false, length = 100)
    private String nombreMascota;

    @Column(nullable = false)
    private Integer edad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private TipoEdad tipoEdad;

    @Column(nullable = false, length = 80)
    private String raza;

    @Enumerated(EnumType.STRING)
    @Column(name = "tamano", nullable = false, length = 20)
    private TamanoMascota tamano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GeneroMascota genero;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 120)
    private String estadoSalud;

    @Column(nullable = false)
    private Boolean vacunado;

    @Column(nullable = false)
    private Boolean esterilizado;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivoEntrega;

    @Column(nullable = false, length = 30)
    private String telefonoContacto;

    @Column(nullable = false, length = 180)
    private String direccion;

    @Column(nullable = false, length = 500)
    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitud estadoSolicitud;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @PrePersist
    void prePersist() {
        if (estadoSolicitud == null) {
            estadoSolicitud = EstadoSolicitud.PENDIENTE;
        }
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
        if (tipoEdad == null) {
            tipoEdad = TipoEdad.ANIOS;
        }
    }
}
