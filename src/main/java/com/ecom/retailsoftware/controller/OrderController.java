package com.ecom.retailsoftware.controller;


import com.ecom.retailsoftware.io.OrderRequest;
import com.ecom.retailsoftware.io.OrderResponse;
import com.ecom.retailsoftware.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
    }

    @GetMapping("/latest")
    public List<OrderResponse> latestOrders(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if (isAdmin) {
            return orderService.getLatestOrders(); // ADMIN: all orders
        }

        // USER: filter by customerName (using the authenticated principal name)
        // Make sure your login username equals the "customerName" saved on orders.
        String currentUserEmail = (auth != null ? auth.getName() : null);
        if (currentUserEmail == null || currentUserEmail.isBlank()) {
            throw new AccessDeniedException("Unauthenticated user");
        }
        return orderService.getLatestOrdersForCustomer(currentUserEmail);
    }
}
