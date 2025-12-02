package com.example.tak.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "option_group")
@Getter
@Setter
@NoArgsConstructor
public class OptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer id;

    @Column(name = "group_key", length = 50, nullable = false)
    private String groupKey;

    @Column(name = "display_name", length = 50, nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "selection_type", nullable = false)
    private SelectionType selectionType=SelectionType.single;

    @Column(name = "min_select", nullable = false)
    private Integer minSelect=0;

    @Column(name = "max_select", nullable = false)
    private Integer maxSelect=1;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder=0;

    @Column(name = "is_required", nullable = false)
    private Boolean required = false;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionValue> values= new ArrayList<>();

    public enum SelectionType {
        single, multi
    }
}
