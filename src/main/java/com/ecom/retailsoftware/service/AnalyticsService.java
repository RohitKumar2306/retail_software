package com.ecom.retailsoftware.service;

import com.ecom.retailsoftware.io.SpendSummaryResponse;

public interface AnalyticsService {
    SpendSummaryResponse spend(String username, int windowDays);
    byte[] exportSpendCsv(String username, int windowDays);
}
