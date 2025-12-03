package com.example.tak.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class MenuOptionGroupId implements Serializable {
    private Integer menuId;
    private Integer groupId;

    @Override
    public int hashCode() {
        return Objects.hash(menuId, groupId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuOptionGroupId that = (MenuOptionGroupId) obj;
        return Objects.equals(menuId, that.menuId) &&
                Objects.equals(groupId, that.groupId);
    }
}
