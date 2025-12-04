package com.example.tak.modules.kiosk.order.repository;

import com.example.tak.common.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OptionValueRepository extends JpaRepository<OptionValue, Integer> {
    // 단순히 그룹 기준 활성 값
    @Query(value = """
        SELECT v.*
        FROM option_value v
        WHERE v.group_id = :groupId
          AND v.is_active = 1
        ORDER BY v.sort_order
        """, nativeQuery = true)
    List<OptionValue> findActiveValuesByGroupId(Integer groupId);

    // 메뉴별 forbid 룰 반영한 값 (원하면 사용)
    @Query(value = """
        SELECT v.*
        FROM option_value v
        WHERE v.group_id = :groupId
          AND v.is_active = 1
          AND NOT EXISTS (
              SELECT 1 FROM menu_option_rule r
              WHERE r.menu_id = :menuId
                AND r.group_id = :groupId
                AND r.value_id = v.value_id
                AND r.rule_type = 'forbid'
          )
        ORDER BY v.sort_order
        """, nativeQuery = true)
    List<OptionValue> findAvailableValuesForMenuAndGroup(Integer menuId, Integer groupId);

    List<OptionValue> findByOptionGroupId(Integer groupId);
}
