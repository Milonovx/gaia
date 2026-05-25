package com.gaia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
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
        name = "refugios",
        indexes = @Index(name = "idx_refugios_ciudad", columnList = "ciudad"),
        uniqueConstraints = @UniqueConstraint(name = "uk_refugios_nombre", columnNames = "nombre")
)
public class Refugio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 180)
    private String direccion;

    @Column(length = 30)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(nullable = false, length = 80)
    private String ciudad;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "refugio", fetch = FetchType.LAZY)
    private List<Mascota> mascotas = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "refugio", fetch = FetchType.LAZY)
    private List<SolicitudIngresoMascota> solicitudesIngreso = new ArrayList<>();
}
