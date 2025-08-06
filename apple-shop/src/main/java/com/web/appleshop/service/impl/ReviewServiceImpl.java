package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.AdminReviewSearchCriteria;
import com.web.appleshop.dto.request.ApproveReviewRequest;
import com.web.appleshop.dto.request.ReplyReviewRequest;
import com.web.appleshop.dto.request.UserCreateReviewRequest;
import com.web.appleshop.dto.response.admin.ReviewAdminDto;
import com.web.appleshop.dto.response.admin.ReviewAdminSummaryDto;
import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.Review;
import com.web.appleshop.entity.Stock;
import com.web.appleshop.entity.User;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.OrderRepository;
import com.web.appleshop.repository.ReviewRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.AdminReviewSearchService;
import com.web.appleshop.service.ReviewService;
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
public class ReviewServiceImpl implements ReviewService {

    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final AdminReviewSearchService adminReviewSearchService;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @Transactional
    public Review userCreateReview(UserCreateReviewRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 3. Validate stock exists and belongs to product
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm nào có id: " + request.getStockId()));


        // 4. Check if user has purchased this stock with DELIVERED status
        boolean hasPurchased = orderRepository.existsByUserIdAndStockIdAndStatus(request.getOrderId(), request.getStockId(), OrderStatus.DELIVERED);

        if (!hasPurchased) {
            throw new BadRequestException("Đơn hàng chưa giao thành công hoặc sản phẩm đánh giá không có trong đơn hàng của bạn");
        }

        // 5. Check if user has already reviewed this stock
        boolean hasReviewed = reviewRepository.existsByUserIdAndStockIdAndOrderId(user.getId(), request.getStockId(), request.getOrderId());

        if (hasReviewed) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này rồi!");
        }

        Order order = orderRepository.findOrderById(request.getOrderId()).orElseThrow(
                () -> new NotFoundException("Không tìm thấy đơn hàng với id: " + request.getOrderId())
        );

        // 6. Create review
        Review review = new Review();
        review.setUser(user);
        review.setOrder(order);
        review.setStock(stock);
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setCreatedAt(LocalDateTime.now());
        review.setIsApproved(false); // Default to false, needs admin approval
        review.getOrder().getOrderDetails().forEach(orderDetail -> {
            orderDetail.setIsReviewed(true);
        });

        return reviewRepository.save(review);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Review approveReview(Integer reviewId) {
        User admin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bản ghi nào có id: " + reviewId));

        if (review.getIsApproved()) {
            review.setApprovedBy(admin);
            review.setApprovedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        } else {
            review.setApprovedBy(null);
            review.setApprovedAt(null);
        }
        review.setIsApproved(!review.getIsApproved());

        return reviewRepository.save(review);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Review replyToReview(Integer reviewId, ReplyReviewRequest request) {
        User admin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy bản ghi nào có id: " + reviewId)
        );
        review.setRepliedBy(admin);
        review.setReplyContent(request.getReplyContent());
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Integer reviewId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy bản ghi nào có id: " + reviewId)
        );
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền xóa bản ghi này!");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Long getReviewCount(LocalDateTime fromDate, LocalDateTime toDate) {
        return reviewRepository.getReviewCount(
                fromDate == null ? LocalDateTime.of(1, 1, 1, 0, 0) : fromDate,
                toDate == null ? LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")) : toDate
        );
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Double getReviewAverage(Integer productId) {
        return reviewRepository.getAverageRatingByProduct(productId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<ReviewAdminSummaryDto> getAllReviewsForAdmin(Pageable pageable, Boolean isApproved) {
        AdminReviewSearchCriteria criteria = new AdminReviewSearchCriteria();
        criteria.setIsApproved(isApproved);
        return adminReviewSearchService.searchReviews(criteria, pageable).map(this::mapToReviewAdminSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ReviewAdminDto getReviewDetail(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy bản ghi nào có id: " + reviewId)
        );
        return mapToReviewAdminDto(review);
    }

    private ReviewAdminSummaryDto mapToReviewAdminSummaryDto(Review review) {
        ReviewAdminSummaryDto dto = new ReviewAdminSummaryDto();
        dto.setId(review.getId());
        dto.setUser(
                new ReviewAdminSummaryDto.UserDto(
                        review.getUser().getId(),
                        review.getUser().getEmail(),
                        review.getUser().getFirstName(),
                        review.getUser().getLastName(),
                        review.getUser().getImage()
                )
        );
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setIsApproved(review.getIsApproved());
        return dto;
    }

    private ReviewAdminDto mapToReviewAdminDto(Review review) {
        ReviewAdminDto dto = new ReviewAdminDto();
        dto.setId(review.getId());
        dto.setUser(
                new ReviewAdminSummaryDto.UserDto(
                        review.getUser().getId(),
                        review.getUser().getEmail(),
                        review.getUser().getFirstName(),
                        review.getUser().getLastName(),
                        review.getUser().getImage()
                )
        );
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setIsApproved(review.getIsApproved());
        dto.setApprovedBy(
                review.getApprovedBy() == null ? null :
                        new ReviewAdminSummaryDto.UserDto(
                                review.getApprovedBy().getId(),
                                review.getApprovedBy().getEmail(),
                                review.getApprovedBy().getFirstName(),
                                review.getApprovedBy().getLastName(),
                                review.getApprovedBy().getImage()
                        )
        );
        dto.setApprovedAt(review.getApprovedAt());
        dto.setRepliedBy(
                review.getRepliedBy() == null ? null :
                        new ReviewAdminSummaryDto.UserDto(
                                review.getRepliedBy().getId(),
                                review.getRepliedBy().getEmail(),
                                review.getRepliedBy().getFirstName(),
                                review.getRepliedBy().getLastName(),
                                review.getRepliedBy().getImage()
                        )
        );
        dto.setReplyContent(review.getReplyContent());
        dto.setStock(
                new ReviewAdminDto.StockDto(
                        review.getStock().getId(),
                        new ReviewAdminDto.StockDto.ProductDto(
                                review.getStock().getProduct().getId(),
                                review.getStock().getProduct().getName()
                        ),
                        review.getStock().getProductPhotos().stream().map(
                                productPhoto -> new ReviewAdminDto.StockDto.ProductPhotoDto(
                                        productPhoto.getId(),
                                        productPhoto.getImageUrl(),
                                        productPhoto.getAlt()
                                )
                        ).collect(Collectors.toSet())
                )
        );

        return dto;
    }
}
