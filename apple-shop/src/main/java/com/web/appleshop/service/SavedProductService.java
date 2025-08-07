package com.web.appleshop.service;

import com.web.appleshop.dto.response.UserSavedProductDto;
import com.web.appleshop.entity.SavedProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SavedProductService {
    SavedProduct saveProduct(Integer stockId);

    void removeSavedProduct(Integer stockId);

    Page<UserSavedProductDto> getMySavedProducts(Pageable pageable);

    Boolean isSaved(Integer stockId);
}
