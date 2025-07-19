package com.web.appleshop.service;

import com.web.appleshop.dto.request.AdminPhotoRequest;
import com.web.appleshop.entity.ProductPhoto;
import org.springframework.web.multipart.MultipartFile;

public interface ProductPhotoService {
    ProductPhoto createProductPhoto(AdminPhotoRequest request, MultipartFile file);

    void deleteProductPhoto(Integer photoId);

    ProductPhoto updateProductPhoto(Integer photoId, AdminPhotoRequest request, MultipartFile file);
}
