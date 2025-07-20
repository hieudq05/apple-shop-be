package com.web.appleshop.service;

import com.web.appleshop.dto.request.UserReviewSearchCriteria;
import com.web.appleshop.dto.response.UserReviewDto;
import com.web.appleshop.entity.Review;
import com.web.appleshop.entity.User;
import com.web.appleshop.repository.ReviewRepository;
import com.web.appleshop.specification.UserReviewSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReviewSearchService {
    private final ReviewRepository reviewRepository;

    // Tìm kiếm reviews cho user đã login
    public Page<UserReviewDto> searchReviewsForUser(UserReviewSearchCriteria criteria, Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReviewSpecification userReviewSpecification = UserReviewSpecification.forUser(user);
        Specification<Review> spec = userReviewSpecification.createSpecification(criteria);
        return reviewRepository.findAll(spec, pageable).map(this::mapToUserReviewDto);
    }

    // Tìm kiếm reviews cho guest user (chưa login)
    public Page<UserReviewDto> searchReviewsForGuest(UserReviewSearchCriteria criteria, Pageable pageable) {
        UserReviewSpecification userReviewSpecification = UserReviewSpecification.forGuest();
        Specification<Review> spec = userReviewSpecification.createSpecification(criteria);
        return reviewRepository.findAll(spec, pageable).map(this::mapToUserReviewDto);
    }

    // Lấy tất cả reviews của user hiện tại (bao gồm cả chưa approved)
    public Page<UserReviewDto> getMyReviews(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReviewSearchCriteria criteria = new UserReviewSearchCriteria();
        // Không set isApproved để lấy tất cả reviews của user
        return reviewRepository.findAll(UserReviewSpecification.forUser(user).createSpecification(criteria), pageable).map(this::mapToUserReviewDto);
    }

    // Lấy reviews của user hiện tại đã được approved
    public Page<UserReviewDto> getMyApprovedReviews(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReviewSearchCriteria criteria = new UserReviewSearchCriteria();
        criteria.setIsApproved(true);
        return reviewRepository.findAll(UserReviewSpecification.forUser(user).createSpecification(criteria), pageable).map(this::mapToUserReviewDto);
    }

    // Lấy reviews của user hiện tại chưa được approved
    public Page<UserReviewDto> getMyPendingReviews(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReviewSearchCriteria criteria = new UserReviewSearchCriteria();
        criteria.setIsApproved(false);
        return reviewRepository.findAll(UserReviewSpecification.forUser(user).createSpecification(criteria), pageable).map(this::mapToUserReviewDto);
    }

    // Lấy reviews của product (chỉ approved + reviews của user hiện tại)
    public Page<UserReviewDto> getProductReviews(Integer productId, Pageable pageable) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReviewSearchCriteria criteria = new UserReviewSearchCriteria();
        criteria.setProductId(productId);

        if (currentUser != null) {
            return searchReviewsForUser(criteria, pageable);
        } else {
            return searchReviewsForGuest(criteria, pageable);
        }
    }

    // Lấy reviews của product cho guest (chỉ approved)
    public Page<UserReviewDto> getProductApprovedReviews(Integer productId, Pageable pageable) {
        UserReviewSearchCriteria criteria = new UserReviewSearchCriteria();
        criteria.setProductId(productId);
        criteria.setIsApproved(true);
        return reviewRepository.findAll(UserReviewSpecification.forGuest().createSpecification(criteria), pageable).map(this::mapToUserReviewDto);
    }

    // Tính average rating của product (chỉ dựa trên approved reviews)
    public double getProductAverageRating(Integer productId) {
        UserReviewSearchCriteria criteria = new UserReviewSearchCriteria();
        criteria.setProductId(productId);
        criteria.setIsApproved(true);
        List<UserReviewDto> approvedReviews = reviewRepository.findAll(UserReviewSpecification.forGuest().createSpecification(criteria)).stream().map(this::mapToUserReviewDto).toList();
        return approvedReviews.stream()
                .mapToInt(UserReviewDto::getRating)
                .average()
                .orElse(0.0);
    }

    // Đếm số reviews của product (chỉ approved)
    public long countProductApprovedReviews(Integer productId) {
        UserReviewSearchCriteria criteria = new UserReviewSearchCriteria();
        criteria.setStockId(productId);
        criteria.setIsApproved(true);
        List<UserReviewDto> approvedReviews = reviewRepository.findAll(UserReviewSpecification.forGuest().createSpecification(criteria)).stream().map(this::mapToUserReviewDto).toList();
        return approvedReviews.size();
    }

    // Kiểm tra user đã review stock chưa
    public boolean hasUserReviewedStock(Integer stockId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return false;
        }

        UserReviewSearchCriteria criteria = new UserReviewSearchCriteria();
        criteria.setStockId(stockId);

        List<Review> userReviews = reviewRepository.findAll(
                UserReviewSpecification.forUser(user).createSpecification(criteria)
        );

        return userReviews.stream()
                .anyMatch(review -> review.getUser().getId().equals(user.getId()));
    }

    public UserReviewDto mapToUserReviewDto(Review review) {
        return new UserReviewDto(
                review.getId(),
                new UserReviewDto.UserDto(
                        review.getUser().getId(),
                        review.getUser().getFirstName(),
                        review.getUser().getLastName(),
                        review.getUser().getImage()
                ),
                review.getContent(),
                review.getRating(),
                review.getCreatedAt(),
                review.getReplyContent(),
                new UserReviewDto.UserDto(
                        review.getRepliedBy().getId(),
                        review.getRepliedBy().getFirstName(),
                        review.getRepliedBy().getLastName(),
                        review.getRepliedBy().getImage()
                ),
                review.getStock().getProduct().getId(),
                review.getStock().getProduct().getName(),
                new UserReviewDto.StockDto(
                        review.getStock().getId(),
                        new UserReviewDto.StockDto.ColorDto(
                                review.getStock().getColor().getId(),
                                review.getStock().getColor().getName(),
                                review.getStock().getColor().getHexCode()
                        ),
                        review.getStock().getInstanceProperties().stream().map(
                                instanceProperty -> new UserReviewDto.StockDto.InstancePropertyDto(
                                        instanceProperty.getId(),
                                        instanceProperty.getName()
                                )
                        ).collect(Collectors.toSet())
                )
        );
    }
}
