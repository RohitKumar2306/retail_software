package com.ecom.retailsoftware.security;

import java.util.Set;
import static com.ecom.retailsoftware.security.AppAuthority.*;

public enum AppRole {
    ROLE_ADMIN(Set.of(
            DASHBOARD_READ, ORDERS_READ, ORDERS_WRITE,
            ITEMS_READ, ITEMS_WRITE, INVENTORY_ADJUST, PRICING_UPDATE
    )),
    ROLE_MANAGER(Set.of(
            DASHBOARD_READ, ORDERS_READ,
            ITEMS_READ, INVENTORY_ADJUST, PRICING_UPDATE
    )),
    ROLE_STOCK_CLERK(Set.of(
            ITEMS_READ, INVENTORY_ADJUST, DASHBOARD_READ
    )),
    ROLE_USER(Set.of(
            DASHBOARD_READ
    ));

    private final Set<AppAuthority> authorities;
    AppRole(Set<AppAuthority> authorities) { this.authorities = authorities; }
    public Set<AppAuthority> getAuthorities() { return authorities; }

}
