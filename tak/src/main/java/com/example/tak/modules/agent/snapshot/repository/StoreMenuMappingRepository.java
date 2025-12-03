package com.example.tak.modules.agent.snapshot.repository;

import com.example.tak.common.StoreMenuMapping;
import com.example.tak.common.StoreMenuMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreMenuMappingRepository extends JpaRepository<StoreMenuMapping, StoreMenuMappingId> {
    List<StoreMenuMapping> findByStoreId(Integer storeId);
    List<StoreMenuMapping> findByMenuId(Integer menuId);
}
