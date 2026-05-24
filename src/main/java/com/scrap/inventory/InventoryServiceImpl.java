package com.scrap.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.scrap.inventory.entity.Inventory;
import com.scrap.inventory.entity.InventoryTransaction;
import com.scrap.inventory.repository.InventoryRepository;
import com.scrap.inventory.repository.InventoryTransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryServiceImpl {

    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private InventoryTransactionRepository txRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    

    // =========================
    // ➕ CUSTOMER → ADD STOCK
    // =========================
    @Transactional
    public void addInventory(Long ownerId, String material, double qty,
                              double price, Long orderId) {

        Inventory inv = inventoryRepo
                .findByOwnerIdAndMaterialType(ownerId, material)
                .orElse(null);

        if (inv == null) {
            inv = new Inventory();
            inv.setOwnerId(ownerId);
            inv.setMaterialType(material);
            inv.setQuantity(qty);
            if (price > 0) {
                inv.setPricePerKg(price);
            }
        } else {
            inv.setQuantity(inv.getQuantity() + qty);
        }

        inventoryRepo.save(inv);
     // LIVE INVENTORY UPDATE
        messagingTemplate.convertAndSend(
                "/topic/inventory/" + ownerId,
                inventoryRepo.findByOwnerId(ownerId)
        );
        saveTransaction(ownerId, material, qty, "ADD", price, orderId, "CUSTOMER");
    }

    // =========================
    // ➖ COMPANY → CHECK STOCK RULE (30kg)
    // =========================
    public boolean canFulfillOrder(Long ownerId, String material, double qty) {

        Inventory inv = inventoryRepo
                .findByOwnerIdAndMaterialType(ownerId, material)
                .orElse(null);

        if (inv == null) return false;

        return inv != null && (inv.getQuantity() >= qty + 5);
    }

    // =========================
    // ➖ COMPANY → REMOVE STOCK
    // =========================
    @Transactional
    public void removeInventory(Long ownerId,
                                String material,
                                double qty,
                                double price,
                                Long orderId) {

        Inventory inv = inventoryRepo
                .findByOwnerIdAndMaterialType(ownerId, material)
                .orElse(null);

        // inventory exists
        if (inv != null) {

            double newQty = inv.getQuantity() - qty;

            if (newQty <= 0) {

                inventoryRepo.delete(inv);

            } else {

                inv.setQuantity(newQty);
                inventoryRepo.save(inv);
            }
        }

        // ALWAYS SAVE TRANSACTION
        saveTransaction(
                ownerId,
                material,
                qty,
                "REMOVE",
                price,
                orderId,
                "COMPANY"
        );

        messagingTemplate.convertAndSend(
                "/topic/inventory/" + ownerId,
                inventoryRepo.findByOwnerId(ownerId)
        );
    }

    // =========================
    // 🔁 TRANSACTION COMMON METHOD
    // =========================
    public void saveTransaction(Long ownerId,
                                String material,
                                double qty,
                                String type,
                                double price,
                                Long refId,
                                String source) {

        InventoryTransaction tx = new InventoryTransaction();

        tx.setOwnerId(ownerId);
        tx.setMaterialType(material);
        tx.setQuantity(qty);
        tx.setType(type); // ADD / REMOVE
        tx.setPricePerKg(price);
        tx.setTotalAmount(qty * price);
        tx.setReferenceId(refId);
        tx.setSource(source); // CUSTOMER / COMPANY

         txRepo.save(tx);

     // LIVE TRANSACTION UPDATE
         messagingTemplate.convertAndSend(
        	        "/topic/transactions/" + ownerId,
        	        txRepo.findByOwnerIdOrderByCreatedAtDesc(ownerId)
        	);
    }

    // =========================
    // 💰 PROFIT
    // =========================
    public double getProfit(Long ownerId) {

        Double sales = txRepo.getTotalSales(ownerId);
        Double purchase = txRepo.getTotalPurchase(ownerId);

        if (sales == null) sales = 0.0;
        if (purchase == null) purchase = 0.0;

        return sales - purchase;
    }
}