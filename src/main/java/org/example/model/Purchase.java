package org.example.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.Objects;

@JsonSerialize
public class Purchase {
    @JsonProperty("transaction_time")
    String transactionTime;

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

    public Purchase() {
    }

    public Purchase(String transactionTime, String productId, BigDecimal price, Integer quantity, Boolean isMember,
                    BigDecimal memberDiscount, Boolean addSupplements, BigDecimal supplementPrice, BigDecimal totalPurchase) {
        this.transactionTime = transactionTime;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.isMember = isMember;
        this.memberDiscount = memberDiscount;
        this.addSupplements = addSupplements;
        this.supplementPrice = supplementPrice;
        this.totalPurchase = totalPurchase;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public Purchase setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public Purchase setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Purchase setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Purchase setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public Boolean getMember() {
        return isMember;
    }

    public Purchase setMember(Boolean member) {
        isMember = member;
        return this;
    }

    public BigDecimal getMemberDiscount() {
        return memberDiscount;
    }

    public Purchase setMemberDiscount(BigDecimal memberDiscount) {
        this.memberDiscount = memberDiscount;
        return this;
    }

    public Boolean getAddSupplements() {
        return addSupplements;
    }

    public Purchase setAddSupplements(Boolean addSupplements) {
        this.addSupplements = addSupplements;
        return this;
    }

    public BigDecimal getSupplementPrice() {
        return supplementPrice;
    }

    public Purchase setSupplementPrice(BigDecimal supplementPrice) {
        this.supplementPrice = supplementPrice;
        return this;
    }

    public BigDecimal getTotalPurchase() {
        return totalPurchase;
    }

    public Purchase setTotalPurchase(BigDecimal totalPurchase) {
        this.totalPurchase = totalPurchase;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase)) return false;
        Purchase purchase = (Purchase) o;
        return getTransactionTime().equals(purchase.getTransactionTime()) && getProductId().equals(purchase.getProductId()) && getPrice().equals(purchase.getPrice()) && getQuantity().equals(purchase.getQuantity()) && isMember.equals(purchase.isMember) && getMemberDiscount().equals(purchase.getMemberDiscount()) && getAddSupplements().equals(purchase.getAddSupplements()) && getSupplementPrice().equals(purchase.getSupplementPrice()) && getTotalPurchase().equals(purchase.getTotalPurchase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTransactionTime(), getProductId(), getPrice(), getQuantity(), isMember, getMemberDiscount(), getAddSupplements(), getSupplementPrice(), getTotalPurchase());
    }
}