package com.ecom.retailsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SpendSummaryResponse {
    private int windowDays;        // 7 or 30
    private String currency;       // "USD" or from config
    private double total;          // sum of all buckets
    private List<SpendBucket> buckets; // ascending by date
}
