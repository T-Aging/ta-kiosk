package com.example.tak.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="store")
@Getter
@Setter
@NoArgsConstructor
public class Store {
    @Id
    @Column(name = "store_id")
    private Integer id;

    @Column(name = "store_name", length = 100)
    private String name;

    @Column(name = "store_address", length = 100)
    private String address;
}
