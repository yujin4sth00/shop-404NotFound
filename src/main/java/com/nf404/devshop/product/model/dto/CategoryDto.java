package com.nf404.devshop.product.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDto {

    private int categoryCode;
    private String categoryName;
    private Integer refCategoryCode;
}
