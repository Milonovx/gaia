package com.gaia.service;

import com.gaia.dto.upload.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    ImageUploadResponse storeImage(MultipartFile file);
}
