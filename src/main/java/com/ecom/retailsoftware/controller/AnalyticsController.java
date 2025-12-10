package com.ecom.retailsoftware.controller;

import com.ecom.retailsoftware.io.SpendSummaryResponse;
import com.ecom.retailsoftware.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // e.g., GET /api/v1.0/analytics/spend?window=30
    @GetMapping("/spend")
    public SpendSummaryResponse spend(@RequestParam(defaultValue = "30") int window,
                                      Principal principal) {
        return analyticsService.spend(principal.getName(), window);
    }

    // e.g., GET /api/v1.0/analytics/spend/export?window=30
    @GetMapping(value = "/spend/export", produces = "text/csv")
    public ResponseEntity<byte[]> export(@RequestParam(defaultValue = "30") int window,
                                         Principal principal) {
        byte[] csv = analyticsService.exportSpendCsv(principal.getName(), window);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=spend_" + window + "d.csv")
                .body(csv);
    }
}
