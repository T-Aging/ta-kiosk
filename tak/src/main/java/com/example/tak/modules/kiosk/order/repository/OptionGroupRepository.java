package com.example.tak.modules.kiosk.order.repository;

import com.example.tak.common.OptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OptionGroupRepository extends JpaRepository<OptionGroup, Integer> {
    // 메뉴 + group_key (temperature / size) 에 해당하는 그룹 1개
    @Query(value = """
            SELECT g.*
            FROM option_group g
            JOIN menu_option_group mog ON g.group_id = mog.group_id
            WHERE mog.menu_id = :menuId
              AND g.group_key = :groupKey
              AND g.is_active = 1
            """, nativeQuery = true)
    Optional<OptionGroup> findGroupForMenuAndKey(Integer menuId, String groupKey);

    // 메뉴에 연결된 상세 옵션 그룹들 (temperature / size 제외)
    @Query(value = """
            SELECT g.*
            FROM option_group g
            JOIN menu_option_group mog ON g.group_id = mog.group_id
            WHERE mog.menu_id = :menuId
              AND g.is_active = 1
              AND g.group_key NOT IN ('temperature', 'size')
            ORDER BY g.sort_order
            """, nativeQuery = true)
    List<OptionGroup> findDetailGroupsForMenu(Integer menuId);
}
