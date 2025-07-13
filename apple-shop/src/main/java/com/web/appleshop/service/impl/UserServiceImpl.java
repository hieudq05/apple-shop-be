package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.request.ChangePasswordDto;
import com.web.appleshop.dto.request.UserSearchCriteria;
import com.web.appleshop.dto.request.UserUpdateDto;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.dto.response.admin.UserAdminInfoDto;
import com.web.appleshop.dto.response.admin.UserAdminSummaryDto;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.RoleRepository;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.UserService;
import com.web.appleshop.specification.UserSpecification;
import com.web.appleshop.util.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.web.appleshop.exception.IllegalArgumentException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UploadUtils uploadUtils;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails findByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    public User findUserByLoginIdentifier(String email) {
        User user = userRepository.getUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return user;
    }

    @Override
    public ProductAdminResponse.ProductOwnerAdminResponse convertUserToProductOwnerAdminResponse(User user) {
        return new ProductAdminResponse.ProductOwnerAdminResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getImage()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfo getUserInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findUserByEnabledAndId(true, user.getId()).orElseThrow(
                () -> new NotFoundException("Không tìm thấy người dùng nào có id: " + user.getId())
        );
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Page<UserAdminSummaryInfo> getListUserSummary(Pageable pageable) {
        return userRepository.findUsersBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public UserAdminInfoDto getUserInfoForAdmin(Integer userId) {
        User user = userRepository.findUserWithRolesById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng nào có id: " + userId));

        return convertToUserAdminInfoDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Page<UserAdminSummaryDto> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        Specification<User> spec = buildSpecification(criteria);

        Page<User> users = userRepository.findAll(spec, pageable);

        return users.map(this::convertToUserAdminSummaryDto);
    }

    @Override
    @Transactional
    public User updateUser(UserUpdateDto userUpdateDto, MultipartFile imageFile) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        user.setPhone(userUpdateDto.getPhone());
        user.setBirth(userUpdateDto.getBirth());
        user.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        if (imageFile != null && !imageFile.isEmpty()) {
            user.setImage(uploadUtils.uploadFile(imageFile));
        } else {
            user.setImage(userUpdateDto.getImage());
        }

        try {
            user = userRepository.save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new org.springframework.dao.DataIntegrityViolationException("Số điện thoại đã tồn tại");
        }
        return user;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public User setRoleforUser(Integer userId, Set<String> roles) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy người dùng nào có id: " + userId)
        );
        user.setRoles(
                roleRepository.findRolesByNameIn(roles)
        );
        return userRepository.save(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public User toggleUserEnabled(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy người dùng nào có id: " + userId)
        );
        user.setEnabled(!user.getEnabled());
        return userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {
        if (!changePasswordDto.getConfirmPassword().equals(changePasswordDto.getNewPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp với mật khẩu mới");
        }

        if (changePasswordDto.getNewPassword().equals(changePasswordDto.getOldPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        userRepository.save(user);
    }

    private Specification<User> buildSpecification(UserSearchCriteria criteria) {
        Specification<User> spec = UserSpecification.createSpecification(criteria);

        // Add sorting if specified in criteria (as an alternative to Pageable sorting)
//        if (StringUtils.hasText(criteria.getSortBy())) {
//            Specification<Product> sortSpec = ProductSpecification.createSortSpecification(
//                    criteria.getSortBy(), criteria.getSortDirection()
//            );
//            assert spec != null;
//            spec = spec.and(sortSpec);
//        }

        return spec;
    }

    private UserAdminInfoDto convertToUserAdminInfoDto(User user) {
        // Convert roles to RoleDto set
        var roleDtos = user.getRoles().stream()
                .map(role -> UserAdminInfoDto.RoleDto.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(java.util.stream.Collectors.toSet());

        return UserAdminInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .image(user.getImage())
                .createdAt(user.getCreatedAt())
                .enabled(user.getEnabled())
                .birth(user.getBirth())
                .roles(roleDtos)
                .build();
    }

    private UserAdminSummaryDto convertToUserAdminSummaryDto(User user) {
        return UserAdminSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .image(user.getImage())
                .enabled(user.getEnabled())
                .birth(user.getBirth())
                .build();
    }
}
