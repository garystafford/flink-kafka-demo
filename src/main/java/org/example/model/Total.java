package org.example.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.Objects;

@JsonSerialize
public class Total {
    @JsonProperty("event_time")
    String eventTime;

    @JsonProperty("product_id")
    String productId;

    @JsonProperty("quantity")
    Integer quantity;

    @JsonProperty("total_purchases")
    BigDecimal totalPurchases;

    public Total() {
    }

    public Total(String eventTime, String productId, Integer quantity, BigDecimal totalPurchases) {
        this.eventTime = eventTime;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPurchases = totalPurchases;
    }

    public String geteventTime() {
        return eventTime;
    }

    public Total seteventTime(String eventTime) {
        this.eventTime = eventTime;
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

    public BigDecimal getTotalPurchases() {
        return totalPurchases;
    }

    public Total setTotalPurchases(BigDecimal totalPurchases) {
        this.totalPurchases = totalPurchases;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Total)) return false;
        Total total = (Total) o;
        return geteventTime().equals(total.geteventTime()) && getProductId().equals(total.getProductId())
                && getQuantity().equals(total.getQuantity()) && getTotalPurchases().equals(total.getTotalPurchases());
    }

    @Override
    public int hashCode() {
        return Objects.hash(geteventTime(), getProductId(), getQuantity(), getTotalPurchases());
    }
}