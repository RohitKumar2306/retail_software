package com.ecom.retailsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SpendBucket {
    private LocalDate date;   // day bucket (yyyy-MM-dd)
    private double amount;    // total spent that day
}