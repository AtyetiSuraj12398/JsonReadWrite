package com.jsonreadandwrite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Root {
    private String transaction_id;
    private LocalDateTime timestamp;
    private String user_id;
    private List<Item> items;
    private int total_before_discount;
    private int total_discount_applied;
    private int final_amount_payable;
    private Payment payment;
    private String location;
    private Shipping shipping;

    // Getters, setters
}







