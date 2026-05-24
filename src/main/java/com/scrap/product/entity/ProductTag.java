package com.scrap.product.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_tags")
public class ProductTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_product_id", nullable = false)
    private Product product;

    @Column(name = "tag")
    private String tag;

    public ProductTag() {
    }

    public ProductTag(Product product, String tag) {
        this.product = product;
        this.tag = tag;
    }

    // ---------------- Getters & Setters ----------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}