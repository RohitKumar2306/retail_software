package com.ecom.retailsoftware.io;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {

    private String name;
    private BigDecimal price;
    private String categoryId;
    private String itemId;
    private String description;
    private Integer stockQuantity;
    private Integer lowStockThreshold = 5;

}
