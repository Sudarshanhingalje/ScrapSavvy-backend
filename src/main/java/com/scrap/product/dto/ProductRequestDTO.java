package com.scrap.product.dto;

import java.util.List;

public class ProductRequestDTO {

    /* ── Basic info ── */
    private String productName;
    private String description;
    private String brand;
    private String model;
    private String condition;

    /* ── Category ── */
    private Long categoryId;
    private String subCategory;

    /* ── Pricing ── */
    private Double price;
    private Double mrp;
    private Integer gst;

    /* ── Inventory ── */
    private Integer quantity;
    private Integer minOrderQty;
    private Integer maxOrderQty;

    /* ── Warranty & origin ── */
    private String warranty;
    private String countryOfOrigin;

    /* ── Shipping ── */
    private Double weight;
    private Double length;
    private Double width;
    private Double height;
    private String fulfilledBy;

    /* ── Specifications stored as JSON string e.g. [{"key":"Color","value":"Red"}] ── */
    private String specifications;

    /* ── Tags ── */
    private List<String> tags;

    public ProductRequestDTO() {
    }

    /* ── Getters & Setters ── */

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getMrp() { return mrp; }
    public void setMrp(Double mrp) { this.mrp = mrp; }

    public Integer getGst() { return gst; }
    public void setGst(Integer gst) { this.gst = gst; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getMinOrderQty() { return minOrderQty; }
    public void setMinOrderQty(Integer minOrderQty) { this.minOrderQty = minOrderQty; }

    public Integer getMaxOrderQty() { return maxOrderQty; }
    public void setMaxOrderQty(Integer maxOrderQty) { this.maxOrderQty = maxOrderQty; }

    public String getWarranty() { return warranty; }
    public void setWarranty(String warranty) { this.warranty = warranty; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }

    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public String getFulfilledBy() { return fulfilledBy; }
    public void setFulfilledBy(String fulfilledBy) { this.fulfilledBy = fulfilledBy; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}