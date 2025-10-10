package com.ecom.retailsoftware.service;

import com.ecom.retailsoftware.io.OrderRequest;
import com.ecom.retailsoftware.io.OrderResponse;
import com.ecom.retailsoftware.io.PaymentVerificationRequest;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(String orderId);

    List<OrderResponse> getLatestOrders();

    OrderResponse verifyPayment(PaymentVerificationRequest request);

    Double sumSalesByDate(LocalDate date);

    Long countByOrderDate(LocalDate date);

    List<OrderResponse> findRecentOrders();
}
