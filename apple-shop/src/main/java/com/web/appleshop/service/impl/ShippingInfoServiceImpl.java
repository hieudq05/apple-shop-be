package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.UserShippingInfoRequest;
import com.web.appleshop.dto.response.UserShippingInfoDto;
import com.web.appleshop.entity.ShippingInfo;
import com.web.appleshop.entity.User;
import com.web.appleshop.repository.ShippingInfoRepository;
import com.web.appleshop.service.ShippingInfoService;
import com.web.appleshop.exception.IllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ShippingInfoServiceImpl implements ShippingInfoService {

    private final ShippingInfoRepository shippingInfoRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ShippingInfo createShippingInfo(UserShippingInfoRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (shippingInfoRepository.findShippingInfoByUserAndWardAndDistrictAndProvinceAndAddress(user, request.getWard(), request.getDistrict(), request.getProvince(), request.getAddress()) != null) {
            throw new IllegalArgumentException("Đã tồn tại thông tin giao hàng này. Vui lòng chọn thông tin khác hoặc xóa thông tin hiện tại trước khi thêm mới.");
        }
        if (request.getIsDefault()) {
            ShippingInfo defaultShippingInfo = shippingInfoRepository.findShippingInfoByUserAndIsDefault(user, true);
            if (defaultShippingInfo != null) {
                defaultShippingInfo.setIsDefault(false);
            }
        }
        ShippingInfo shippingInfo = new ShippingInfo();
        BeanUtils.copyProperties(request, shippingInfo);
        shippingInfo.setUser(user);
        return shippingInfoRepository.save(shippingInfo);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ShippingInfo updateShippingInfo(Integer shippingInfoId, UserShippingInfoRequest request) {
        ShippingInfo shippingInfo = shippingInfoRepository.findById(shippingInfoId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy thông tin giao hàng.")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ShippingInfo duplicateShippingInfo = shippingInfoRepository.findShippingInfoByUserAndWardAndDistrictAndProvinceAndAddress(user, request.getWard(), request.getDistrict(), request.getProvince(), request.getAddress());
        if (duplicateShippingInfo != null && !duplicateShippingInfo.getId().equals(shippingInfoId)) {
            throw new IllegalArgumentException("Đã tồn tại thông tin giao hàng này. Vui lòng chọn thông tin khác hoặc xóa thông tin hiện tại trước khi thêm mới.");
        } else if (!shippingInfo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Không tồn tại thông tin giao hàng của bạn.");
        }

        if (request.getIsDefault()) {
            ShippingInfo defaultShippingInfo = shippingInfoRepository.findShippingInfoByUserAndIsDefault(user, true);
            if (defaultShippingInfo != null) {
                defaultShippingInfo.setIsDefault(false);
            }
        }

        BeanUtils.copyProperties(request, shippingInfo);
        shippingInfo.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        return shippingInfoRepository.save(shippingInfo);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public void deleteShippingInfo(Integer shippingInfoId) {
        ShippingInfo shippingInfo = shippingInfoRepository.findById(shippingInfoId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy thông tin giao hàng.")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!shippingInfo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Không tồn tại thông tin giao hàng của bạn.");
        } else {
            shippingInfoRepository.delete(shippingInfo);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public Page<UserShippingInfoDto> getMyShippingInfo(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return shippingInfoRepository.findShippingInfosByUser(user, pageable).map(this::mapToUserShippingInfoDto);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ShippingInfo setDefaultShippingInfo(Integer shippingInfoId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ShippingInfo shippingInfo = shippingInfoRepository.findById(shippingInfoId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy thông tin giao hàng.")
        );
        if (!shippingInfo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Không tồn tại thông tin giao hàng của bạn.");
        }
        ShippingInfo defaultShippingInfo = shippingInfoRepository.findShippingInfoByUserAndIsDefault(user, true);
        if (defaultShippingInfo != null) {
            defaultShippingInfo.setIsDefault(false);
        }
        shippingInfo.setIsDefault(true);

        return shippingInfoRepository.save(shippingInfo);
    }

    private UserShippingInfoDto mapToUserShippingInfoDto(ShippingInfo shippingInfo) {
        UserShippingInfoDto dto = new UserShippingInfoDto();
        BeanUtils.copyProperties(shippingInfo, dto);
        return dto;
    }
}
