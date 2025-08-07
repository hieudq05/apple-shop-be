package com.web.appleshop.service.impl;

import com.web.appleshop.dto.response.ProductUserResponse;
import com.web.appleshop.dto.response.UserSavedProductDto;
import com.web.appleshop.entity.SavedProduct;
import com.web.appleshop.entity.SavedProductId;
import com.web.appleshop.entity.Stock;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.IllegalArgumentException;
import com.web.appleshop.repository.SavedProductRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.SavedProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedProductServiceImpl implements SavedProductService {

    private final StockRepository stockRepository;
    private final SavedProductRepository savedProductRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public SavedProduct saveProduct(Integer stockId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Stock stockToSave = stockRepository.findById(stockId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy sản phẩm nào có id: " + stockId)
        );

        if (stockToSave.getProduct().getIsDeleted()) {
            throw new IllegalArgumentException("Sản phẩm đã bị xóa.");
        }
        SavedProductId savedProductId = new SavedProductId();
        savedProductId.setStockId(stockId);
        savedProductId.setUserId(user.getId());

        return savedProductRepository.save(new SavedProduct(
                savedProductId,
                user,
                stockToSave,
                stockToSave.getProduct(),
                LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        ));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public void removeSavedProduct(Integer stockId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Stock stockToRemove = stockRepository.findById(stockId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy sản phẩm nào có id: " + stockId)
        );
        savedProductRepository.deleteSavedProductByUserIdAndStockId(user.getId(), stockToRemove.getId());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public Page<UserSavedProductDto> getMySavedProducts(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<SavedProduct> savedProducts = savedProductRepository.findSavedProductsByUserAndProduct_IsDeleted(user, false, pageable);
        return savedProducts.map(this::mapToUserSavedProductDto);
    }

    @Override
    public Boolean isSaved(Integer stockId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SavedProductId savedProductId = new SavedProductId();
        savedProductId.setStockId(stockId);
        savedProductId.setUserId(user.getId());
        return savedProductRepository.existsById(savedProductId);
    }

    private UserSavedProductDto mapToUserSavedProductDto(SavedProduct savedProduct) {
        UserSavedProductDto dto = new UserSavedProductDto();
        dto.setStock(
                new UserSavedProductDto.StockDto(
                        savedProduct.getStock().getId(),
                        new UserSavedProductDto.StockDto.ProductDto(
                                savedProduct.getStock().getProduct().getId(),
                                savedProduct.getStock().getProduct().getName()
                        ),
                        savedProduct.getStock().getProduct().getCategory().getId(),
                        new ProductUserResponse.ProductStockResponse.StockColorResponse(
                                savedProduct.getStock().getColor().getId(),
                                savedProduct.getStock().getColor().getName(),
                                savedProduct.getStock().getColor().getHexCode()
                        ),
                        savedProduct.getStock().getPrice(),
                        savedProduct.getStock().getProductPhotos().stream().map(photo ->
                                new ProductUserResponse.ProductStockResponse.StockPhotoResponse(
                                        photo.getId(),
                                        photo.getImageUrl(),
                                        photo.getAlt()
                                )
                        ).collect(Collectors.toSet()),
                        savedProduct.getStock().getInstanceProperties().stream().map(instance ->
                                new ProductUserResponse.ProductStockResponse.StockInstanceResponse(
                                        instance.getId(),
                                        instance.getName()
                                )
                        ).collect(Collectors.toSet())
                )
        );
        dto.setCreatedAt(savedProduct.getCreatedAt());

        return dto;
    }
}
