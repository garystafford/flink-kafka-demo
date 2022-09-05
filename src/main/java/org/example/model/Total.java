package org.example.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
public class Total {
    @JsonProperty("transaction_time")
    String transactionTime;

    @JsonProperty("product_id")
    String productId;

    @JsonProperty("quantity")
    Integer quantity;

    @JsonProperty("total_purchases")
    Float totalPurchases;

    public String getTransactionTime() {
        return transactionTime;
    }

    public Total setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public Total setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Total setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public Float getTotalPurchases() {
        return totalPurchases;
    }

    public Total setTotalPurchases(Float totalPurchases) {
        this.totalPurchases = totalPurchases;
        return this;
    }

    public Total() {
    }

    public Total(String transactionTime, String productId, Integer quantity, Float totalPurchases) {
        this.transactionTime = transactionTime;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPurchases = totalPurchases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Total)) return false;
        Total total = (Total) o;
        return getTransactionTime().equals(total.getTransactionTime()) && getProductId().equals(total.getProductId())
                && getQuantity().equals(total.getQuantity()) && getTotalPurchases().equals(total.getTotalPurchases());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTransactionTime(), getProductId(), getQuantity(), getTotalPurchases());
    }
}