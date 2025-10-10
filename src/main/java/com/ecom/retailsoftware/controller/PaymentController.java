package com.ecom.retailsoftware.controller;


import com.ecom.retailsoftware.io.OrderResponse;
import com.ecom.retailsoftware.io.PaymentRequest;
import com.ecom.retailsoftware.io.PaymentVerificationRequest;
import com.ecom.retailsoftware.io.RazorpayOrderResponse;
import com.ecom.retailsoftware.service.OrderService;
import com.ecom.retailsoftware.service.RazorpayService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;
    private final OrderService orderService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public RazorpayOrderResponse createOrder(@RequestBody PaymentRequest request) throws RazorpayException {
        return razorpayService.createOrder(request.getAmount(), request.getCurrency());
    }

    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request) {
        return orderService.verifyPayment(request);
    }

}
