package com.web.appleshop.service;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.dto.response.admin.UserAdminInfoDto;
import com.web.appleshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDetails findByEmail(String email);

    User findUserByLoginIdentifier(String email);

    void save(User user);

    ProductAdminResponse.ProductOwnerAdminResponse convertUserToProductOwnerAdminResponse(User user);

    UserInfo getUserInfo();

    Page<UserAdminSummaryInfo> getListUserSummary(Pageable pageable);

    UserAdminInfoDto getUserInfoForAdmin(Integer userId);

}
