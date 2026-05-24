package com.scrap.scraporder.dto;

import java.time.LocalDate;
import java.util.List;

public class CustomerSellOrderRequest {

    private String pickupAddress;
    private String contactNo;

    private LocalDate pickupDate;
    private String pickupTime;
    
    private String customerName;

    private List<CustomerSellItemDTO> items;

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public LocalDate  getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public List<CustomerSellItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CustomerSellItemDTO> items) {
        this.items = items;
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}