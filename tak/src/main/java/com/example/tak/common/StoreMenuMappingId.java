package com.example.tak.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class StoreMenuMappingId implements Serializable {
    private Integer storeId;
    private Integer menuId;

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null||getClass() != obj.getClass()) return false;
        StoreMenuMappingId that=(StoreMenuMappingId) obj;
        return Objects.equals(storeId, that.storeId) &&
                Objects.equals(menuId, that.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, menuId);
    }
}
