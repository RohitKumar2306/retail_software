package com.ecom.retailsoftware.service.impl;

import com.ecom.retailsoftware.io.SpendBucket;
import com.ecom.retailsoftware.io.SpendSummaryResponse;
import com.ecom.retailsoftware.repository.OrderEntityRepository;
import com.ecom.retailsoftware.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderEntityRepository orderRepo;

    @Override
    public SpendSummaryResponse spend(String username, int windowDays) {
        int wd = (windowDays == 7) ? 7 : 30; // default to 30 if not 7
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime since = startOfToday.minusDays(wd - 1);

        List<Object[]> rows = orderRepo.spendPerDaySince(username, since);

        // Seed a map with every day in window as 0
        LinkedHashMap<LocalDate, Double> perDay = new LinkedHashMap<>();
        for (int i = 0; i < wd; i++) {
            perDay.put(LocalDate.now().minusDays(wd - 1 - i), 0.0);
        }

        // Fill actual data
        for (Object[] row : rows) {
            // row[0] is java.sql.Date or LocalDate (depending on dialect)
            LocalDate day = (row[0] instanceof java.sql.Date d) ? d.toLocalDate() : (LocalDate) row[0];
            double amt = ((Number) row[1]).doubleValue();
            perDay.put(day, amt);
        }

        List<SpendBucket> buckets = perDay.entrySet().stream()
                .map(e -> new SpendBucket(e.getKey(), e.getValue()))
                .toList();

        double total = buckets.stream().mapToDouble(SpendBucket::getAmount).sum();

        return SpendSummaryResponse.builder()
                .windowDays(wd)
                .currency("USD")
                .total(total)
                .buckets(buckets)
                .build();
    }

    @Override
    public byte[] exportSpendCsv(String username, int windowDays) {
        SpendSummaryResponse s = spend(username, windowDays);
        StringBuilder sb = new StringBuilder("date,amount\n");
        s.getBuckets().forEach(b -> sb.append(b.getDate()).append(',').append(b.getAmount()).append('\n'));
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}