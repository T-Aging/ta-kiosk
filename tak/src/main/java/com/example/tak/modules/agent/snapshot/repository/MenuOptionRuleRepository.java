package com.example.tak.modules.agent.snapshot.repository;

import com.example.tak.common.MenuOptionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuOptionRuleRepository extends JpaRepository<MenuOptionRule, Integer> {
    List<MenuOptionRule> findByMenuId(Integer menuId);

    @Query("""
        SELECT r
        FROM MenuOptionRule r
        JOIN FETCH r.optionValue v
        WHERE r.menu.id = :menuId
          AND r.optionGroup.id = :groupId
    """)
    List<MenuOptionRule> findByMenuAndGroup(
            @Param("menuId") Integer menuId,
            @Param("groupId") Integer groupId
    );

    List<MenuOptionRule> findByMenuIdAndOptionGroupId(Integer menuId, Integer groupId);
}
