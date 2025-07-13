package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.CreatePromotionRequest;
import com.web.appleshop.dto.request.PromotionSearchRequest;
import com.web.appleshop.dto.request.UpdatePromotionRequest;
import com.web.appleshop.dto.response.UserPromotionDto;
import com.web.appleshop.dto.response.UserReviewDto;
import com.web.appleshop.dto.response.admin.AdminPromotionDto;
import com.web.appleshop.entity.Promotion;
import com.web.appleshop.entity.User;
import com.web.appleshop.enums.PromotionType;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.PromotionRepository;
import com.web.appleshop.service.PromotionService;
import com.web.appleshop.specification.PromotionSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private static final Logger log = LoggerFactory.getLogger(PromotionServiceImpl.class);
    private final PromotionRepository promotionRepository;
    private final PromotionSpecification promotionSpecification;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public AdminPromotionDto createPromotion(CreatePromotionRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 1. Validate promotion code uniqueness
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Promotion code already exists");
        }

        // 2. Validate date range
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        if (request.getEndDate().isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))) {
            throw new BadRequestException("End date must be after current date");
        }

        // 3. Create promotion entity
        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setCode(request.getCode());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setValue(request.getValue());
        promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setUsageCount(0);
        promotion.setIsActive(request.getIsActive());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setCreatedBy(user);
        promotion.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        // 5. Save promotion
        Promotion savedPromotion = promotionRepository.save(promotion);

        return mapToResponse(savedPromotion);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public AdminPromotionDto updatePromotion(Integer id, UpdatePromotionRequest request) {
        // 1. Find existing promotion
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promotion not found"));

        // 2. Validate date range
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        // 3. Update promotion fields
        promotion.setName(request.getName());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setValue(request.getValue());
        promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());

        // 6. Save updated promotion
        Promotion updatedPromotion = promotionRepository.save(promotion);

        return mapToResponse(updatedPromotion);
    }

    @Transactional
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public void deletePromotion(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promotion not found"));

        // Check if promotion is being used
        if (promotion.getUsageCount() > 0) {
            throw new BadRequestException("Cannot delete promotion that has been used");
        }

        promotionRepository.delete(promotion);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public void togglePromotionStatus(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promotion not found"));

        promotion.setIsActive(!promotion.getIsActive());
        promotionRepository.save(promotion);
    }

    @Override
    public Promotion findValidPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCodeAndIsActive(code, true).orElseThrow(
                () -> new BadRequestException("Mã giảm giá " + code + " không tồn tại hoặc đã hết hạn.")
        );

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new BadRequestException("Mã giảm giá " + code + " đã hết hạn.");
        }
        if (promotion.getUsageLimit() != null && promotion.getUsageCount() >= promotion.getUsageLimit()) {
            throw new BadRequestException("Mã giảm giá " + code + " đã được sử dụng quá số lượng.");
        }

        return promotion;
    }

    @Override
    public BigDecimal calculateDiscountAmount(Promotion promotion, BigDecimal amount) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (promotion.getPromotionType() == PromotionType.PERCENTAGE) {
            discountAmount = amount.multiply(promotion.getValue().divide(new BigDecimal("100")));
            if (promotion.getMaxDiscountAmount() != null &&
                    discountAmount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
                discountAmount = promotion.getMaxDiscountAmount();
            }
        } else if (promotion.getPromotionType() == PromotionType.FIXED_AMOUNT) {
            discountAmount = promotion.getValue();
            if (discountAmount.compareTo(amount) > 0) {
                discountAmount = amount;
            }
        } else if (promotion.getPromotionType() == PromotionType.SHIPPING_DISCOUNT) {
            BigDecimal maxDiscountAmount = promotion.getMaxDiscountAmount() != null ? promotion.getMaxDiscountAmount() : amount;
            discountAmount = amount.multiply(promotion.getValue().divide(new BigDecimal("100")));
        }

        return discountAmount;
    }

    @Override
    public void incrementUsageCount(Promotion promotion) {
        promotion.setUsageCount(promotion.getUsageCount() + 1);
        promotionRepository.save(promotion);
    }

    @Override
    public boolean isPromotionValid(Promotion promotion, BigDecimal orderValue) {
        return promotion.getMinOrderValue() == null ||
                orderValue.compareTo(promotion.getMinOrderValue()) >= 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminPromotionDto> searchPromotions(PromotionSearchRequest request,
                                                    Pageable pageable) {
        // Tạo Sort object
        Sort sort = createSort(request.getSortBy(), request.getSortDirection());

        // Tạo Pageable
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // Tạo Specification
        Specification<Promotion> spec = promotionSpecification.searchPromotions(request);

        // Thực hiện search
        Page<Promotion> promotions = promotionRepository.findAll(spec, pageable);

        return promotions.map(this::mapToResponse);
    }

    @Override
    public Page<UserPromotionDto> getPromotionsForUser(Pageable pageable) {
        Sort sort = createSort("id", "DESC");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Promotion> promotions = promotionRepository.findPromotionsByIsActive(true, pageable);
        return promotions.map(this::mapToUserResponse);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        if (!StringUtils.hasText(sortBy)) {
            sortBy = "id";
        }

        Sort.Direction direction = Sort.Direction.DESC;
        if (StringUtils.hasText(sortDirection) && "ASC".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.ASC;
        }

        return Sort.by(direction, sortBy);
    }

    private UserPromotionDto mapToUserResponse(Promotion promotion) {
        UserPromotionDto response = new UserPromotionDto();
        response.setName(promotion.getName());
        response.setCode(promotion.getCode());
        response.setPromotionType(promotion.getPromotionType());
        response.setValue(promotion.getValue());
        response.setMaxDiscountAmount(promotion.getMaxDiscountAmount());
        response.setMinOrderValue(promotion.getMinOrderValue());
        response.setStartDate(promotion.getStartDate());
        response.setEndDate(promotion.getEndDate());
        return response;
    }

    private AdminPromotionDto mapToResponse(Promotion promotion) {
        AdminPromotionDto response = new AdminPromotionDto();
        response.setId(promotion.getId());
        response.setName(promotion.getName());
        response.setCode(promotion.getCode());
        response.setPromotionType(promotion.getPromotionType());
        response.setValue(promotion.getValue());
        response.setMaxDiscountAmount(promotion.getMaxDiscountAmount());
        response.setMinOrderValue(promotion.getMinOrderValue());
        response.setUsageLimit(promotion.getUsageLimit());
        response.setUsageCount(promotion.getUsageCount());
        response.setIsActive(promotion.getIsActive());
        response.setStartDate(promotion.getStartDate());
        response.setEndDate(promotion.getEndDate());
        response.setCreatedAt(promotion.getCreatedAt());
        response.setCreatedBy(new UserReviewDto.UserDto(
                promotion.getCreatedBy().getId(),
                promotion.getCreatedBy().getFirstName(),
                promotion.getCreatedBy().getLastName(),
                promotion.getCreatedBy().getImage()
        ));

        return response;
    }
}
