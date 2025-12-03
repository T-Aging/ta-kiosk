package com.example.tak.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_option_group")
@Getter
@Setter
@NoArgsConstructor
@IdClass(MenuOptionGroupId.class)
public class MenuOptionGroup {

    @Id
    @Column(name = "menu_id")
    private Integer menuId;

    @Id
    @Column(name = "group_id")
    private Integer groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", insertable = false, updatable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private OptionGroup optionGroup;
}
