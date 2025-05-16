package com.jsonreadandwrite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    public String item;
    public int price;
    public int quantity;
    public String category;
    public String item_uuid;
    public double item_subtotal;
    public double potential_discount;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(item_uuid, item.item_uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(item_uuid);
    }
}