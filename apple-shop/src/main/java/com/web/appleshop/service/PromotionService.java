package com.web.appleshop.service;

import com.web.appleshop.dto.request.CreatePromotionRequest;
import com.web.appleshop.dto.request.PromotionSearchRequest;
import com.web.appleshop.dto.request.UpdatePromotionRequest;
import com.web.appleshop.dto.response.UserPromotionDto;
import com.web.appleshop.dto.response.admin.AdminPromotionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromotionService {
    AdminPromotionDto createPromotion(CreatePromotionRequest request);

    Page<AdminPromotionDto> searchPromotions(PromotionSearchRequest request,
                                             Pageable pageable);

    AdminPromotionDto updatePromotion(Integer id, UpdatePromotionRequest request);

    void deletePromotion(Integer id);

    void togglePromotionStatus(Integer id);
}
