package com.jsonreadandwrite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipping {
    public String carrier;
    public String status;
    public LocalDate delivery_estimate;
}