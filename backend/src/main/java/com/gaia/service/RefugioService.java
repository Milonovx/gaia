package com.gaia.service;

import com.gaia.dto.refugio.RefugioRequest;
import com.gaia.dto.refugio.RefugioResponse;
import java.util.List;

public interface RefugioService {

    List<RefugioResponse> findAll();

    RefugioResponse findById(Long id);

    RefugioResponse create(RefugioRequest request);

    RefugioResponse update(Long id, RefugioRequest request);

    void delete(Long id);
}
