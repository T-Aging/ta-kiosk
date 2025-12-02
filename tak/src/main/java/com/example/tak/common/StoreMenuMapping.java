package com.example.tak.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "store_menu_mapping")
@Getter
@Setter
@NoArgsConstructor
@IdClass(StoreMenuMapping.class)
public class StoreMenuMapping {

    @Id
    @Column(name = "storeId")
    private Integer storeId;

    @Id
    @Column(name = "menu_id")
    private Integer menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", insertable = false, updatable = false)
    private Menu menu;
}

@Getter
@Setter
@NoArgsConstructor
class StoreMenuMappingId implements Serializable{
    private Integer storeId;
    private Integer menuId;
}
