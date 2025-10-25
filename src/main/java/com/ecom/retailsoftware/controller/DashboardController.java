package com.ecom.retailsoftware.controller;


import com.ecom.retailsoftware.io.DashboardResponse;
import com.ecom.retailsoftware.io.ItemResponse;
import com.ecom.retailsoftware.io.OrderResponse;
import com.ecom.retailsoftware.service.ItemService;
import com.ecom.retailsoftware.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrderService orderService;
    private final ItemService itemService;

    @GetMapping
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        Double todaySale = orderService.sumSalesByDate(today);
        Long todayOrderCount = orderService.countByOrderDate(today);
        List<OrderResponse> recentOrders = orderService.findRecentOrders();
        return new DashboardResponse(
                todaySale != null ? todaySale : 0.0,
                todayOrderCount != null ? todayOrderCount : 0,
                recentOrders
        );
    }

    @GetMapping("/lowStock")
    public List<ItemResponse> getLowStockItems() {
        return itemService.getLowStockItems(5);
    }

    @GetMapping("/outOfStock")
    public List<ItemResponse> getOutOfStockItems() {
        return itemService.getOutOfStockItems();
    }


}
