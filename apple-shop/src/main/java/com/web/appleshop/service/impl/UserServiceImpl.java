package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.dto.response.admin.UserAdminInfoDto;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

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
    public void save(User user) {
        userRepository.save(user);
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<UserAdminSummaryInfo> getListUserSummary(Pageable pageable) {
        return userRepository.findUsersBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public UserAdminInfoDto getUserInfoForAdmin(Integer userId) {
        User user = userRepository.findUserWithRolesById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng nào có id: " + userId));

        return convertToUserAdminInfoDto(user);
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
}
