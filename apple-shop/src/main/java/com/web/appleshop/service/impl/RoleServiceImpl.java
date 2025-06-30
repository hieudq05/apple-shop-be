package com.web.appleshop.service.impl;

import com.web.appleshop.entity.Role;
import com.web.appleshop.repository.RoleRepository;
import com.web.appleshop.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Set<Role> findRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }
}
