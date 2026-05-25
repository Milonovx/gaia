package com.gaia.service.impl;

import com.gaia.dto.refugio.RefugioRequest;
import com.gaia.dto.refugio.RefugioResponse;
import com.gaia.entity.Refugio;
import com.gaia.exception.ResourceNotFoundException;
import com.gaia.mapper.RefugioMapper;
import com.gaia.repository.RefugioRepository;
import com.gaia.service.RefugioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefugioServiceImpl implements RefugioService {

    private final RefugioRepository refugioRepository;
    private final RefugioMapper refugioMapper;

    public RefugioServiceImpl(RefugioRepository refugioRepository, RefugioMapper refugioMapper) {
        this.refugioRepository = refugioRepository;
        this.refugioMapper = refugioMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefugioResponse> findAll() {
        return refugioRepository.findAll().stream()
                .map(refugioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RefugioResponse findById(Long id) {
        return refugioMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public RefugioResponse create(RefugioRequest request) {
        Refugio refugio = refugioMapper.toEntity(request);
        return refugioMapper.toResponse(refugioRepository.save(refugio));
    }

    @Override
    @Transactional
    public RefugioResponse update(Long id, RefugioRequest request) {
        Refugio refugio = findEntityById(id);
        refugioMapper.updateEntity(refugio, request);
        return refugioMapper.toResponse(refugioRepository.save(refugio));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Refugio refugio = findEntityById(id);
        refugioRepository.delete(refugio);
    }

    private Refugio findEntityById(Long id) {
        return refugioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refugio no encontrado con id " + id));
    }
}
