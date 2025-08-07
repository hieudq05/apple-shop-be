package com.web.appleshop.repository;

import com.web.appleshop.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Integer> , JpaSpecificationExecutor<Color> {
    Optional<Color> findByName(String name);
}