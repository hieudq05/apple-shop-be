package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.ApproveReviewRequest;
import com.web.appleshop.dto.request.ReplyReviewRequest;
import com.web.appleshop.dto.request.UserCreateReviewRequest;
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
import com.web.appleshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

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

        return reviewRepository.save(review);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Review approveReview(Integer reviewId, ApproveReviewRequest request) {
        User admin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bản ghi nào có id: " + reviewId));

        if(request.getApproved()) {
            review.setIsApproved(true);
            review.setApprovedBy(admin);
            review.setApprovedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        } else {
            review.setIsApproved(false);
            review.setApprovedBy(null);
            review.setApprovedAt(null);
        }

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
}
