package com.jsonreadandwrite;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jsonreadandwrite.model.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import java.time.temporal.ChronoUnit;


public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Load discount config
        Map<String, Double> discountMap = objectMapper.readValue(
                new File("src/main/resources/discount_config.json"),
                new TypeReference<>() {}
        );

        // Load transactions
        List<Root> rootList = objectMapper.readValue(
                new File("src/main/resources/transactions_preprocessing.json"),
                new TypeReference<>() {}
        );

        Set<String> seenItemUUIDs = new HashSet<>();
        List<Transactions> validTransctions = new ArrayList<>();
        List<Root> invalidUUID = new ArrayList<>();
        List<Root> invalidDelivery = new ArrayList<>();
        List<Root> invalidDiscount = new ArrayList<>();


//        check for delivery
        rootList.forEach(root -> {

            long deliveryGap = ChronoUnit.DAYS.between(
                    root.getTimestamp().toLocalDate(),
                    root.getShipping().getDelivery_estimate()
            );
            if (deliveryGap > 7) {
                invalidDelivery.add(root);
                return;
            }

            // Check for item uuid
            boolean hasDuplicate = root.getItems().stream()
                    .map(Item::getItem_uuid)
                    .anyMatch(uuid -> !seenItemUUIDs.add(uuid));
            if (hasDuplicate) {
                invalidUUID.add(root);
                return;
            }


            int totalBeforeDiscount = root.getItems().stream()
                    .mapToInt(item -> item.getPrice() * item.getQuantity())
                    .sum();

            double discountApplied = root.getItems().stream()
                    .mapToDouble(item -> {
                        double categoryDiscount = discountMap.getOrDefault(item.getCategory(), 0.0);
                        return item.getPrice() * item.getQuantity() * categoryDiscount;
                    })
                    .sum();

            double finalAmount = root.getItems().stream()
                    .mapToDouble(item -> {
                        double categoryDiscount = discountMap.getOrDefault(item.getCategory(), 0.0);
                        return item.getPrice() * item.getQuantity() * (1 - categoryDiscount);
                    })
                    .sum();

            // Validate for discont
            boolean hasExcessDiscount = root.getItems().stream()
                    .anyMatch(item -> item.getPotential_discount() / 100.0 >
                            discountMap.getOrDefault(item.getCategory(), 0.0));

            if (hasExcessDiscount) {
                invalidDiscount.add(root);
                return;
            }

            // Set calculated values
            root.setTotal_before_discount(totalBeforeDiscount);
            root.setTotal_discount_applied((int) discountApplied);
            root.setFinal_amount_payable((int) finalAmount);

            // Add valid transaction
            validTransctions.add(new Transactions(
                    root.getTransaction_id(),
                    root.getTotal_before_discount(),
                    root.getTotal_discount_applied(),
                    root.getFinal_amount_payable()
            ));
        });

        // Write output
        objectMapper.writeValue(new File("src/main/resources/transactions.json"), validTransctions);
        objectMapper.writeValue(new File("src/main/resources/invalid_uuid.json"), invalidUUID);
        objectMapper.writeValue(new File("src/main/resources/invalid_delivery.json"), invalidDelivery);
        objectMapper.writeValue(new File("src/main/resources/invalid_discount.json"), invalidDiscount);


//=================== for testing only ===========================
//
//                List<Item> listItem = Stream.of(
//                new Item("brush", 22.0, 2, "electronic", "123", 22.0, 11.0),
//                new Item("brush", 1.0, 28, "electronic", "123", 12.0, 11.0)
//
//                ).collect(Collectors.toList());
//
//        List<Root> listTest = Stream.of(
//                new Root("12", new Date("12/12/12"), "123", (ArrayList<Item>) listItem,0, 0, 0, new Payment(20, "Dollar", "UPI"),"Pune",  new Shipping("qw","wq","wer")),
//                new Root("12", new Date("12/12/12"), "123", (ArrayList<Item>) listItem,0, 0, 0, new Payment(20, "Dollar", "UPI"),"Pune",  new Shipping("qw","wq","wer"))
//
//        ).toList();
//        listTest.stream().distinct();
//
//
//
//        System.out.println(listTest.stream().mapToDouble(Root::getFinal_amount_payable).sum());

    }


}
