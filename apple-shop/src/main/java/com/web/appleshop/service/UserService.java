package com.web.appleshop.service;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.request.UserSearchCriteria;
import com.web.appleshop.dto.request.UserUpdateDto;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.dto.response.admin.UserAdminInfoDto;
import com.web.appleshop.dto.response.admin.UserAdminSummaryDto;
import com.web.appleshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface UserService {
    UserDetails findByEmail(String email);

    User findUserByLoginIdentifier(String email);

    ProductAdminResponse.ProductOwnerAdminResponse convertUserToProductOwnerAdminResponse(User user);

    UserInfo getUserInfo();

    Page<UserAdminSummaryInfo> getListUserSummary(Pageable pageable);

    UserAdminInfoDto getUserInfoForAdmin(Integer userId);

    Page<UserAdminSummaryDto> searchUsers(UserSearchCriteria criteria, Pageable pageable);

    User updateUser(UserUpdateDto userUpdateDto, MultipartFile imageFile);

    User setRoleforUser(Integer userId, Set<String> roles);

    User toggleUserEnabled(Integer userId);
}
