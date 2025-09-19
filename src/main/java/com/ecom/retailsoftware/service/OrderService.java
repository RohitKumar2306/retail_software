package com.ecom.retailsoftware.service;

import com.ecom.retailsoftware.io.OrderRequest;
import com.ecom.retailsoftware.io.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(String orderId);

    List<OrderResponse> getLatestOrders();
}
