package com.scrap.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.scrap.dto.OrderInvoiceDTO;
//import com.scrap.dto.ScrapyardOrderResponseDTO;
//import com.scrap.dto.UpdateOrderStatusRequest;
import com.scrap.order.OrderService;
import com.scrap.order.dto.OrderInvoiceDTO;
import com.scrap.order.dto.UpdateOrderStatusRequest;
import com.scrap.product.dto.ProductRequestDTO;
import com.scrap.product.dto.ProductResponseDTO;
import com.scrap.scraporder.dto.ScrapyardOrderResponseDTO;

@RestController
@RequestMapping("/api/scrapyard")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Injected as a Spring bean — more efficient than new ObjectMapper() per request
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProductService proOrderService;
//    
    @Autowired
    private OrderService orderService;
    

    // ADD PRODUCT
    @PostMapping(
            value = "/addproduct/{userProfileId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProductResponseDTO addProduct(
            @PathVariable Long userProfileId,
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) throws Exception {

        ProductRequestDTO dto = objectMapper.readValue(productJson, ProductRequestDTO.class);
        return productService.createProduct(userProfileId, dto, images);
    }

    // UPDATE PRODUCT
    @PutMapping(
            value = "/updateproduct/{userProfileId}/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProductResponseDTO updateProduct(
            @PathVariable Long userProfileId,
            @PathVariable Long productId,
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) throws Exception {

        ProductRequestDTO dto = objectMapper.readValue(productJson, ProductRequestDTO.class);
        return productService.updateProduct(userProfileId, productId, dto, images);
    }

    // OWNER PRODUCTS
    @GetMapping("/getmyproducts/{userProfileId}")
    public List<ProductResponseDTO> getOwnerProducts(
            @PathVariable Long userProfileId
    ) {
        return productService.getOwnerProducts(userProfileId);
    }

    // ALL ACTIVE PRODUCTS
    @GetMapping("/getallproducts")
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllActiveProducts();
    }

    // DELETE
    @DeleteMapping("/deleteproduct/{userProfileId}/{productId}")
    public String deleteProduct(
            @PathVariable Long userProfileId,
            @PathVariable Long productId
    ) {
        productService.deleteProduct(userProfileId, productId);
        return "Product deleted successfully";
    }
    
    
    @GetMapping("/orders")
    public ResponseEntity<List<ScrapyardOrderResponseDTO>>
    getAllScrapyardOrders() {

        return ResponseEntity.ok(
        		proOrderService.getAllScrapyardOrders()
        );
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ScrapyardOrderResponseDTO>
    getOrderById(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
        		proOrderService.getOrderById(orderId)
        );
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ScrapyardOrderResponseDTO>
    updateOrderStatus(
            @PathVariable Long orderId,

            @RequestBody
            UpdateOrderStatusRequest request
    ) {

        return ResponseEntity.ok(

        		proOrderService.updateOrderStatus(
                        orderId,
                        request.getStatus()
                )
        );
    }
    
    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<OrderInvoiceDTO> getInvoice(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderInvoice(orderId));
    }
    
    
//    @PatchMapping("/orders/{orderId}/clear")
//    public ResponseEntity<Void> clearOrder(
//            @PathVariable Long orderId
//    ) {
//        proOrderService.clearOrder(orderId);
//        return ResponseEntity.ok().build();
//    }
}