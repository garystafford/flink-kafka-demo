package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.flink.table.annotation.DataTypeHint;

import java.math.BigDecimal;
import java.sql.Timestamp;

@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PurchaseEnriched {
    @JsonProperty("transaction_time")
    String transactionTime;

    @JsonProperty("transaction_timestamp")
    Timestamp transactionTimestamp;

    @JsonProperty("transaction_id")
    String transactionId;

    @JsonProperty("product_id")
    String productId;

    @JsonProperty("product_category")
    String productCategory;

    @JsonProperty("product_name")
    String productName;

    @JsonProperty("product_size")
    String productSize;

    @DataTypeHint("DECIMAL(10,2)")
    @JsonProperty("product_cogs")
    BigDecimal productCOGS;

    @DataTypeHint("DECIMAL(10,2)")
    @JsonProperty("product_price")
    BigDecimal productPrice;

    @JsonProperty("contains_fruit")
    Boolean containsFruit;

    @JsonProperty("contains_veggies")
    Boolean containsVeggies;

    @JsonProperty("contains_nuts")
    Boolean containsNuts;

    @JsonProperty("contains_caffeine")
    Boolean containsCaffeine;

    @DataTypeHint("DECIMAL(10,2)")
    @JsonProperty("purchase_price")
    BigDecimal purchasePrice;

    @JsonProperty("purchase_quantity")
    Integer purchaseQuantity;

    @JsonProperty("is_member")
    Boolean isMember;

    @DataTypeHint("DECIMAL(10,2)")
    @JsonProperty("member_discount")
    BigDecimal memberDiscount;

    @JsonProperty("add_supplements")
    Boolean addSupplements;

    @DataTypeHint("DECIMAL(10,2)")
    @JsonProperty("supplement_price")
    BigDecimal supplementPrice;

    @DataTypeHint("DECIMAL(10,2)")
    @JsonProperty("total_purchase")
    BigDecimal totalPurchase;
}