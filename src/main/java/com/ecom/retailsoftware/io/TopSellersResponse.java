package com.ecom.retailsoftware.io;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSellersResponse {
        String username;
        Double totalSpent;
        Long ordersCount;
        LocalDateTime lastOrderAt;
}
