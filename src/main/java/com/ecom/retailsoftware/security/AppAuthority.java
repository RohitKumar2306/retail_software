package com.ecom.retailsoftware.security;

public enum AppAuthority {
    DASHBOARD_READ,        // view admin dashboard
    ORDERS_READ, ORDERS_WRITE,
    ITEMS_READ, ITEMS_WRITE,
    INVENTORY_ADJUST,      // change stock levels
    PRICING_UPDATE         // change price
}
