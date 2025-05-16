package com.jsonreadandwrite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {

    private String transactionId;
   private int total_before_discount;
    private int total_discount_applied;
    private int final_amount_payable;

}
