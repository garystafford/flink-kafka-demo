package org.example;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@JsonSerialize
public class Purchase {
    LocalDateTime transactionTime;
    String productId;
    Float price;
    Integer quantity;
    Boolean isMember;
    Float memberDiscount;
    Boolean addSupplements;
    Float supplementPrice;
    Float totalPurchase;
}