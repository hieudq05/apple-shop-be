package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.AdminPhotoRequest;
import com.web.appleshop.entity.ProductPhoto;
import com.web.appleshop.repository.ProductPhotoRepository;
import com.web.appleshop.service.ProductPhotoService;
import com.web.appleshop.util.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductPhotoServiceImpl implements ProductPhotoService {
    private final UploadUtils uploadUtils;
    private final ProductPhotoRepository productPhotoRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ProductPhoto createProductPhoto(AdminPhotoRequest request, MultipartFile file) {
        ProductPhoto productPhoto = new ProductPhoto();
        if (file != null && !file.isEmpty()) {
            productPhoto.setImageUrl(uploadUtils.uploadFile(file));
        } else {
            productPhoto.setImageUrl(request.getImageUrl());
        }
        productPhoto.setAlt(request.getAlt());
        return productPhotoRepository.save(productPhoto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public void deleteProductPhoto(Integer photoId) {
        productPhotoRepository.deleteById(photoId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ProductPhoto updateProductPhoto(Integer photoId, AdminPhotoRequest request, MultipartFile file) {
        ProductPhoto productPhoto = productPhotoRepository.findById(photoId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy hình ảnh.")
        );
        if (file != null && !file.isEmpty()) {
            productPhoto.setImageUrl(uploadUtils.uploadFile(file));
        } else {
            productPhoto.setImageUrl(request.getImageUrl());
        }
        productPhoto.setAlt(request.getAlt());
        return productPhotoRepository.save(
                productPhoto
        );
    }
}
