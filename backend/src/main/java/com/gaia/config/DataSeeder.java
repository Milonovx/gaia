package com.gaia.config;

import com.gaia.entity.Refugio;
import com.gaia.entity.Role;
import com.gaia.entity.User;
import com.gaia.repository.RefugioRepository;
import com.gaia.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedInitialData(
            RefugioRepository refugioRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedRefugio(
                    refugioRepository,
                    "Huellitas del Sur",
                    "Comuna 6, Neiva",
                    "+57 310 458 9211",
                    "Refugio comunitario dedicado al rescate y cuidado de mascotas.",
                    "2.9273",
                    "-75.2819"
            );
            seedRefugio(
                    refugioRepository,
                    "Fundación Patitas Neiva",
                    "Barrio Canaima, Neiva",
                    "+57 311 672 1843",
                    "Fundación local enfocada en rehabilitación y adopción responsable.",
                    "2.9381",
                    "-75.2875",
                    "Fundación Patitas Neiva"
            );
            seedRefugio(
                    refugioRepository,
                    "Hogar Animal Gaia",
                    "Malecón Río Magdalena, Neiva",
                    "+57 320 918 4475",
                    "Hogar de paso para animales rescatados en Neiva.",
                    "2.9345",
                    "-75.2952"
            );

            if (!userRepository.existsByEmail("admin@gaia.local")) {
                userRepository.save(User.builder()
                        .nombre("Administrador")
                        .apellido("GAIA")
                        .email("admin@gaia.local")
                        .password(passwordEncoder.encode("Admin12345"))
                        .telefono("+57 300 000 0000")
                        .role(Role.ROLE_ADMIN)
                        .build());
            }
        };
    }

    private void seedRefugio(
            RefugioRepository repository,
            String nombre,
            String direccion,
            String telefono,
            String descripcion,
            String latitud,
            String longitud,
            String... legacyNames
    ) {
        Refugio refugio = findExistingRefugio(repository, nombre, legacyNames)
                .orElseGet(() -> Refugio.builder().nombre(nombre).build());
        refugio.setNombre(nombre);
        refugio.setDireccion(direccion);
        refugio.setTelefono(telefono);
        refugio.setDescripcion(descripcion);
        refugio.setLatitud(new BigDecimal(latitud));
        refugio.setLongitud(new BigDecimal(longitud));
        refugio.setCiudad("Neiva");
        repository.save(refugio);
    }

    private Optional<Refugio> findExistingRefugio(RefugioRepository repository, String nombre, String... legacyNames) {
        Optional<Refugio> current = repository.findByNombre(nombre);
        if (current.isPresent()) {
            return current;
        }
        for (String legacyName : legacyNames) {
            Optional<Refugio> legacy = repository.findByNombre(legacyName);
            if (legacy.isPresent()) {
                return legacy;
            }
        }
        return Optional.empty();
    }
}
