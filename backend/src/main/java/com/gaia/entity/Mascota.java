package com.gaia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        name = "mascotas",
        indexes = {
                @Index(name = "idx_mascotas_refugio", columnList = "refugio_id"),
                @Index(name = "idx_mascotas_disponible", columnList = "disponible"),
                @Index(name = "idx_mascotas_genero", columnList = "genero"),
                @Index(name = "idx_mascotas_tamano", columnList = "tamano")
        }
)
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

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

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 120)
    private String estadoSalud;

    @Column(nullable = false)
    private Boolean vacunado;

    @Column(nullable = false)
    private Boolean esterilizado;

    @Column(length = 500)
    private String imagenUrl;

    @Column(nullable = false)
    private Boolean disponible;

    private LocalDate fechaRescate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "refugio_id", nullable = false)
    private Refugio refugio;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SolicitudAdopcion> solicitudesAdopcion = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (disponible == null) {
            disponible = true;
        }
        if (tipoEdad == null) {
            tipoEdad = TipoEdad.ANIOS;
        }
    }
}
