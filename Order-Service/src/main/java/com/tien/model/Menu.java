package com.tien.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    private Long menuId;

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
}
