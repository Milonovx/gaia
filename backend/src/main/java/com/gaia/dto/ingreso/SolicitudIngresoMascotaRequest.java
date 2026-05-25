package com.gaia.dto.ingreso;

import com.gaia.entity.GeneroMascota;
import com.gaia.entity.TamanoMascota;
import com.gaia.entity.TipoEdad;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SolicitudIngresoMascotaRequest {

    @NotBlank
    @Size(max = 100)
    private String nombreMascota;

    @NotNull
    @Min(0)
    @Max(40)
    private Integer edad;

    @NotNull
    private TipoEdad tipoEdad;

    @NotBlank
    @Size(max = 80)
    private String raza;

    @NotNull
    private TamanoMascota tamano;

    @NotNull
    private GeneroMascota genero;

    @NotBlank
    @Size(max = 2000)
    private String descripcion;

    @NotBlank
    @Size(max = 120)
    private String estadoSalud;

    @NotNull
    private Boolean vacunado;

    @NotNull
    private Boolean esterilizado;

    @NotBlank
    @Size(max = 2000)
    private String motivoEntrega;

    @NotBlank
    @Size(max = 30)
    private String telefonoContacto;

    @NotBlank
    @Size(max = 180)
    private String direccion;

    @NotNull
    private Long refugioId;

    @NotNull
    private MultipartFile imagen;
}
