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
public class Product {
    @JsonProperty("event_time")
    String transactionTime;

    @JsonProperty("product_id")
    String productId;

    @JsonProperty("category")
    String category;

    @JsonProperty("item")
    String item;

    @JsonProperty("size")
    String size;

    @JsonProperty("cogs")
    BigDecimal cogs;

    @JsonProperty("price")
    BigDecimal price;

    @JsonProperty("inventory")
    Integer inventory;

    @JsonProperty("contains_fruit")
    Boolean containsFruit;

    @JsonProperty("contains_veggies")
    Boolean containsVeggies;

    @JsonProperty("contains_nuts")
    Boolean containsNuts;

    @JsonProperty("contains_caffeine")
    Boolean containsCaffeine;

    @JsonProperty("propensity_to_buy")
    int propensityToBuy;
}