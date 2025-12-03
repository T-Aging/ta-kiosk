package com.example.tak.modules.agent.snapshot.repository;

import com.example.tak.common.MenuOptionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOptionRuleRepository extends JpaRepository<MenuOptionRule, Integer> {
    List<MenuOptionRule> findByMenuId(Integer menuId);

    List<MenuOptionRule> findByMenuIdAndOptionGroupId(Integer menuId, Integer groupId);
}
