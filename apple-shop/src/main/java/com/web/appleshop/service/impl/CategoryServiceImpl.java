package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.dto.request.AdminCategoryRequest;
import com.web.appleshop.dto.request.CategorySearchCriteria;
import com.web.appleshop.dto.response.CategoryWProductResponse;
import com.web.appleshop.dto.response.admin.CategoryInfoDto;
import com.web.appleshop.entity.Category;
import com.web.appleshop.repository.CategoryRepository;
import com.web.appleshop.service.CategoryService;
import com.web.appleshop.specification.CategorySpecification;
import com.web.appleshop.util.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategorySpecification categorySpecification;
    private final UploadUtils uploadUtils;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<CategoryInfoView> getCategoriesForAdmin(Pageable pageable) {
        return categoryRepository.findAllBy(pageable);
    }

    @Override
    public List<CategoryWProductResponse> getCategories() {
        List<Category> categories = categoryRepository.findCategoryWith4Products();
        return categories.stream().map(this::mapToCategoryWProductResponse).toList();
    }

    @Override
    public CategoryInfoView getCategoryById(Integer categoryId) {
        return categoryRepository.findCategoryById(categoryId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy danh mục.")
        );

    }

    @Override
    public Page<CategoryInfoDto> searchCategoryForAdmin(CategorySearchCriteria criteria, Pageable pageable) {
        Specification<Category> spec = categorySpecification.createSpecification(criteria);
        Page<Category> categories = categoryRepository.findAll(spec, pageable);
        return categories.map(this::convertCategoryToCategoryInfoDto);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Category createCategory(AdminCategoryRequest request, MultipartFile file) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (file != null && !file.isEmpty()) {
            category.setImage(uploadUtils.uploadFile(file));
        } else {
            category.setImage(request.getImage());
        }
        return categoryRepository.save(category);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Category updateCategory(Integer categoryId, AdminCategoryRequest request, MultipartFile file) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy danh mục.")
        );
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (file != null && !file.isEmpty()) {
            category.setImage(uploadUtils.uploadFile(file));
        } else {
            category.setImage(request.getImage());
        }
        return categoryRepository.save(
                category
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public void deleteCategory(Integer categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    private CategoryInfoDto convertCategoryToCategoryInfoDto(Category category) {
        return new CategoryInfoDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImage()
        );
    }

    private CategoryWProductResponse mapToCategoryWProductResponse(Category category) {
        return new CategoryWProductResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImage(),
                category.getProducts().stream().limit(4).sorted(
                        (p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())
                ).map(product ->
                new CategoryWProductResponse.ProductInfo(
                        product.getId(),
                        product.getName()
                )).toList().toArray(new CategoryWProductResponse.ProductInfo[0])
        );
    }
}
