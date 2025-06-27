package com.web.appleshop.service;

import com.web.appleshop.entity.Role;

import java.util.Set;

public interface RoleService {
    Set<Role> findRoleByName(String name);
}
