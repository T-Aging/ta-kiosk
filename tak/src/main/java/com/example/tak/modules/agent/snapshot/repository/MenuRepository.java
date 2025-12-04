package com.example.tak.modules.agent.snapshot.repository;

import com.example.tak.common.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Query("""
            select m
            from Menu m
            join StoreMenuMapping sm on sm.menu.id = m.id
            where sm.store.id = :storeId
            order by m.id
            """)
    List<Menu> findMenusByStoreId(@Param("storeId") Integer storeId);

    Optional<Menu> findByName(String name);
}
