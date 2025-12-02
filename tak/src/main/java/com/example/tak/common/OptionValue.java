package com.example.tak.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "option_value")
@Getter
@Setter
@NoArgsConstructor
public class OptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "value_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private OptionGroup optionGroup;

    @Column(name = "value_key", length = 50, nullable = false)
    private String valueKey;

    @Column(name = "display_name", length = 50, nullable = false)
    private String displayName;

    @Column(name = "extra_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal extraPrice;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder=0;

    @Column(name = "is_active", nullable = false)
    private Boolean active=true;
}
