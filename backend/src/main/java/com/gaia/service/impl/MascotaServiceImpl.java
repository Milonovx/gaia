package com.gaia.service.impl;

import com.gaia.dto.mascota.MascotaRequest;
import com.gaia.dto.mascota.MascotaResponse;
import com.gaia.entity.GeneroMascota;
import com.gaia.entity.Mascota;
import com.gaia.entity.Refugio;
import com.gaia.entity.TamanoMascota;
import com.gaia.exception.ApiException;
import com.gaia.exception.ResourceNotFoundException;
import com.gaia.mapper.MascotaMapper;
import com.gaia.repository.MascotaRepository;
import com.gaia.repository.RefugioRepository;
import com.gaia.repository.SolicitudAdopcionRepository;
import com.gaia.service.MascotaService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;
    private final RefugioRepository refugioRepository;
    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final MascotaMapper mascotaMapper;

    public MascotaServiceImpl(
            MascotaRepository mascotaRepository,
            RefugioRepository refugioRepository,
            SolicitudAdopcionRepository solicitudAdopcionRepository,
            MascotaMapper mascotaMapper
    ) {
        this.mascotaRepository = mascotaRepository;
        this.refugioRepository = refugioRepository;
        this.solicitudAdopcionRepository = solicitudAdopcionRepository;
        this.mascotaMapper = mascotaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponse> findAll(String raza, String tamano, String genero, Boolean disponible) {
        return mascotaRepository.findAll(buildSpecification(raza, tamano, genero, disponible)).stream()
                .map(mascotaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponse findById(Long id) {
        return mascotaMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public MascotaResponse create(MascotaRequest request) {
        Refugio refugio = findRefugioById(request.refugioId());
        Mascota mascota = mascotaMapper.toEntity(request, refugio);
        return mascotaMapper.toResponse(mascotaRepository.save(mascota));
    }

    @Override
    @Transactional
    public MascotaResponse update(Long id, MascotaRequest request) {
        Mascota mascota = findEntityById(id);
        Refugio refugio = findRefugioById(request.refugioId());
        mascotaMapper.updateEntity(mascota, request, refugio);
        return mascotaMapper.toResponse(mascotaRepository.save(mascota));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Mascota mascota = findEntityById(id);
        solicitudAdopcionRepository.deleteByMascota(mascota);
        mascotaRepository.delete(mascota);
    }

    private Specification<Mascota> buildSpecification(String raza, String tamano, String genero, Boolean disponible) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(raza)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("raza")), raza.trim().toLowerCase(Locale.ROOT)));
            }
            if (StringUtils.hasText(tamano)) {
                predicates.add(criteriaBuilder.equal(root.get("tamano"), parseEnum(TamanoMascota.class, tamano)));
            }
            if (StringUtils.hasText(genero)) {
                predicates.add(criteriaBuilder.equal(root.get("genero"), parseEnum(GeneroMascota.class, genero)));
            }
            if (disponible != null) {
                predicates.add(criteriaBuilder.equal(root.get("disponible"), disponible));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Mascota findEntityById(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con id " + id));
    }

    private Refugio findRefugioById(Long id) {
        return refugioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refugio no encontrado con id " + id));
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ApiException("Filtro inválido: " + value, HttpStatus.BAD_REQUEST);
        }
    }
}
