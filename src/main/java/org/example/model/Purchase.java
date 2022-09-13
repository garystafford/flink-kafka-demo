package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Purchase {
    @JsonProperty("transaction_time")
    String transactionTime;

    @JsonProperty("transaction_id")
    String transactionId;

    @JsonProperty("product_id")
    String productId;

    @JsonProperty("price")
    BigDecimal price;

    @JsonProperty("quantity")
    Integer quantity;

    @JsonProperty("is_member")
    Boolean isMember;

    @JsonProperty("member_discount")
    BigDecimal memberDiscount;

    @JsonProperty("add_supplements")
    Boolean addSupplements;

    @JsonProperty("supplement_price")
    BigDecimal supplementPrice;

    @JsonProperty("total_purchase")
    BigDecimal totalPurchase;
}