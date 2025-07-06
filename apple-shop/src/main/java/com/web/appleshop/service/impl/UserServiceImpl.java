package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.request.UserSearchCriteria;
import com.web.appleshop.dto.request.UserUpdateDto;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.dto.response.admin.UserAdminInfoDto;
import com.web.appleshop.dto.response.admin.UserAdminSummaryDto;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.UserService;
import com.web.appleshop.specification.UserSpecification;
import com.web.appleshop.util.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UploadUtils uploadUtils;

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

    @Override
    public Page<UserAdminSummaryDto> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        Specification<User> spec = buildSpecification(criteria);

        Page<User> users = userRepository.findAll(spec, pageable);

        return users.map(this::convertToUserAdminSummaryDto);
    }

    @Override
    public User updateUser(UserUpdateDto userUpdateDto, MultipartFile imageFile) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        user.setPhone(userUpdateDto.getPhone());
        user.setBirth(userUpdateDto.getBirth());
        if (imageFile != null && !imageFile.isEmpty()) {
            user.setImage(uploadUtils.uploadFile(imageFile));
        } else {
            user.setImage(userUpdateDto.getImage());
        }
        return userRepository.save(user);
    }

    public static boolean isSimpleCriteria(UserSearchCriteria criteria) {
        return criteria.getId() == null &&
                criteria.getEmail() == null &&
                criteria.getPhone() == null &&
                criteria.getName() == null &&
                criteria.getBirthFrom() == null &&
                criteria.getBirthTo() == null &&
                criteria.getEnabled() == null &&
                (criteria.getRoleName() == null || criteria.getRoleName().isEmpty()) &&
                criteria.getCreatedAtFrom() == null &&
                criteria.getCreatedAtTo() == null;

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
