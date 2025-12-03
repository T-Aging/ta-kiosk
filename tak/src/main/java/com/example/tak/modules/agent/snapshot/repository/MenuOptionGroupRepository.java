package com.example.tak.modules.agent.snapshot.repository;

import com.example.tak.common.MenuOptionGroup;
import com.example.tak.common.MenuOptionGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOptionGroupRepository extends JpaRepository<MenuOptionGroup, MenuOptionGroupId> {
    List<MenuOptionGroup> findByMenuId(Integer menuId);
}
