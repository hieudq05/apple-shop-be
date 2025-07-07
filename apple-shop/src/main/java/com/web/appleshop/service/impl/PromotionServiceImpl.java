package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.CreatePromotionRequest;
import com.web.appleshop.dto.request.PromotionSearchRequest;
import com.web.appleshop.dto.request.UpdatePromotionRequest;
import com.web.appleshop.dto.response.UserPromotionDto;
import com.web.appleshop.dto.response.UserReviewDto;
import com.web.appleshop.dto.response.admin.AdminPromotionDto;
import com.web.appleshop.entity.Category;
import com.web.appleshop.entity.Promotion;
import com.web.appleshop.entity.Stock;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.CategoryRepository;
import com.web.appleshop.repository.PromotionRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.PromotionService;
import com.web.appleshop.specification.PromotionSpecification;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final CategoryRepository categoryRepository;
    private final PromotionSpecification promotionSpecification;
    private final StockRepository stockRepository;

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
        promotion.setApplyOn(request.getApplyOn());
        promotion.setCreatedBy(user);
        promotion.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        // 4. Set categories and stocks if applyOn is true
        if (request.getApplyOn()) {
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
                if (categories.size() != request.getCategoryIds().size()) {
                    throw new BadRequestException("Some categories not found");
                }
                categories.stream().map(Category::getId).forEach(System.out::println);
                promotion.setCategories(categories);
            }

            if (request.getStockIds() != null && !request.getStockIds().isEmpty()) {
                Set<Stock> stocks = new HashSet<>(stockRepository.findAllById(request.getStockIds()));
                if (stocks.size() != request.getStockIds().size()) {
                    throw new BadRequestException("Some stocks not found");
                }
                promotion.setStocks(stocks);
            }
        }

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
        promotion.setApplyOn(request.getApplyOn());

        // 4. Clear existing associations
        promotion.getCategories().clear();
        promotion.getStocks().clear();

        // 5. Set new associations if applyOn is true
        if (request.getApplyOn()) {
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
                if (categories.size() != request.getCategoryIds().size()) {
                    throw new BadRequestException("Some categories not found");
                }
                promotion.setCategories(categories);
            }

            if (request.getStockIds() != null && !request.getStockIds().isEmpty()) {
                Set<Stock> stocks = new HashSet<>(stockRepository.findAllById(request.getStockIds()));
                if (stocks.size() != request.getStockIds().size()) {
                    throw new BadRequestException("Some stocks not found");
                }
                promotion.setStocks(stocks);
            }
        }

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

    @Transactional(readOnly = true)
    @Override
    public Page<UserPromotionDto> searchPromotionsForUser(Integer productIds, Integer categoryId, Pageable pageable) {
        PromotionSearchRequest criteria = new PromotionSearchRequest();
        criteria.setProductIds(new ArrayList<>(productIds));
        criteria.setCategoryIds(new ArrayList<>(categoryId));
        criteria.setIsActive(true);
        Specification<Promotion> spec = promotionSpecification.searchPromotions(criteria);
        Page<Promotion> promotions = promotionRepository.findAll(spec, pageable);
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
        response.setApplyOn(promotion.getApplyOn());
        response.setCreatedAt(promotion.getCreatedAt());
        response.setCreatedBy(new UserReviewDto.UserDto(
                promotion.getCreatedBy().getId(),
                promotion.getCreatedBy().getFirstName(),
                promotion.getCreatedBy().getLastName(),
                promotion.getCreatedBy().getImage()
        ));

        // Map categories
        if (promotion.getCategories() != null) {
            response.setCategories(promotion.getCategories().stream()
                    .map(category -> {
                        AdminPromotionDto.CategorySummary summary = new AdminPromotionDto.CategorySummary();
                        summary.setId(category.getId());
                        summary.setName(category.getName());
                        return summary;
                    })
                    .collect(Collectors.toList()));
        }

        // Map stocks
        if (promotion.getStocks() != null) {
            response.setStocks(promotion.getStocks().stream()
                    .map(stock -> {
                        AdminPromotionDto.StockSummary summary = new AdminPromotionDto.StockSummary();
                        summary.setId(stock.getId());
                        summary.setColorName(stock.getColor().getName());
                        summary.setInstances(stock.getInstanceProperties().stream().map(
                                instance -> {
                                    AdminPromotionDto.StockSummary.InstanceSummary instanceSummary = new AdminPromotionDto.StockSummary.InstanceSummary();
                                    instanceSummary.setId(instance.getId());
                                    instanceSummary.setName(instance.getName());
                                    return instanceSummary;
                                }
                        ).collect(Collectors.toSet()));
                        return summary;
                    })
                    .collect(Collectors.toList()));
        }

        return response;
    }
}
