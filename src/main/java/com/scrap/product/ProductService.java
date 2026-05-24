package com.scrap.product;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

//import com.scrap.dto.ScrapyardOrderResponseDTO;
import com.scrap.product.dto.ProductRequestDTO;
import com.scrap.product.dto.ProductResponseDTO;
import com.scrap.scraporder.dto.ScrapyardOrderResponseDTO;

public interface ProductService {

    ProductResponseDTO createProduct(
            Long userProfileId,
            ProductRequestDTO dto,
            MultipartFile[] images
    );

    ProductResponseDTO updateProduct(
            Long userProfileId,
            Long productId,
            ProductRequestDTO dto,
            MultipartFile[] images
    );

    List<ProductResponseDTO> getOwnerProducts(Long userProfileId);

    List<ProductResponseDTO> getAllActiveProducts();

    void deleteProduct(Long userProfileId, Long productId);

    List<ScrapyardOrderResponseDTO> getAllScrapyardOrders();

    ScrapyardOrderResponseDTO getOrderById(Long orderId);

    ScrapyardOrderResponseDTO updateOrderStatus(
            Long orderId,
            String status
    );
       
}