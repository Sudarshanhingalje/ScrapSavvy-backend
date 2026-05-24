package com.scrap.product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scrap.auth.entity.UserProfile;
import com.scrap.auth.repository.UserProfileRepository;
import com.scrap.common.enums.OrderStatus;
import com.scrap.common.exception.ResourceNotFoundException;
import com.scrap.order.dto.OrderItemDTO;
//import com.scrap.dto.OrderItemDTO;
//import com.scrap.dto.ScrapyardOrderResponseDTO;
import com.scrap.order.entity.OrderEntity;
import com.scrap.order.repository.OrderItemRepository;
import com.scrap.order.repository.OrderRepository;
import com.scrap.product.dto.ProductRequestDTO;
import com.scrap.product.dto.ProductResponseDTO;
import com.scrap.product.entity.Category;
import com.scrap.product.entity.Product;
import com.scrap.product.entity.ProductImage;
import com.scrap.product.repository.CategoryRepository;
import com.scrap.product.repository.ProductImageRepository;
import com.scrap.product.repository.ProductRepository;
import com.scrap.product.repository.ProductTagRepository;
import com.scrap.scraporder.dto.ScrapyardOrderResponseDTO;

import jakarta.transaction.Transactional;



@Service
public class ProductServiceImpl implements ProductService {

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + "/uploads/products/";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private ProductTagRepository productTagRepository;

    @Autowired
    private OrderRepository orderRepository;
    
    
    @Override
    @Transactional
    public ProductResponseDTO createProduct(
            Long userProfileId,
            ProductRequestDTO dto,
            MultipartFile[] images
    ) {
        UserProfile userProfile = userProfileRepository
                .findById(userProfileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User profile not found"));

        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        mapDtoToProduct(dto, product);
        product.setCategory(category);
        product.setUserProfile(userProfile);
        product.setActive(true);

        Product saved = productRepository.save(product);
        saveImages(saved, images);

        return mapToDTO(productRepository
                .findById(saved.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")));
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(
            Long userProfileId,
            Long productId,
            ProductRequestDTO dto,
            MultipartFile[] images
    ) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (!product.getUserProfile().getUserProfileId().equals(userProfileId)) {
            throw new ResourceNotFoundException("Unauthorized");
        }

        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        mapDtoToProduct(dto, product);
        product.setCategory(category);

        if (images != null && images.length > 0) {
            product.getImages().clear();
            saveImages(product, images);
        }

        return mapToDTO(productRepository.save(product));
    }

    @Override
    public List<ProductResponseDTO> getOwnerProducts(Long userProfileId) {
        return productRepository
                .findByUserProfile_UserProfileIdOrderByCreatedAtDesc(userProfileId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> getAllActiveProducts() {
        return productRepository
                .findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

   

    @Override
    @Transactional
    public void deleteProduct(Long userProfileId,Long productId) {


        // DELETE order items FIRST
        orderItemRepository.deleteByProduct_ProductId(productId);

        // DELETE product tags
        productTagRepository.deleteByProductProductId(productId);

        // DELETE product images
        productImageRepository.deleteByProductId(productId);

        // DELETE product
        productRepository.deleteById(productId);
    }

    /* ── Private: map DTO fields onto a Product entity ── */
    private void mapDtoToProduct(ProductRequestDTO dto, Product product) {
        /* Basic info */
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setBrand(dto.getBrand());
        product.setModel(dto.getModel());
        product.setCondition(dto.getCondition());

        /* Category */
        product.setSubCategory(dto.getSubCategory());

        /* Pricing */
        product.setPrice(dto.getPrice());
        product.setMrp(dto.getMrp());
        product.setGst(dto.getGst());

        /* Inventory */
        product.setQuantity(dto.getQuantity());
        product.setMinOrderQty(dto.getMinOrderQty() != null ? dto.getMinOrderQty() : 1);
        product.setMaxOrderQty(dto.getMaxOrderQty());

        /* Warranty & origin */
        product.setWarranty(dto.getWarranty());
        product.setCountryOfOrigin(dto.getCountryOfOrigin());

        /* Shipping */
        product.setWeight(dto.getWeight());
        product.setLength(dto.getLength());
        product.setWidth(dto.getWidth());
        product.setHeight(dto.getHeight());
        product.setFulfilledBy(dto.getFulfilledBy());

        /* Specifications & tags */
        product.setSpecifications(dto.getSpecifications());
        product.setTags(dto.getTags() != null ? dto.getTags() : new ArrayList<>());
    }

    /* ── Private: save uploaded image files to disk ── */
    private void saveImages(Product product, MultipartFile[] images) {
        if (images == null || images.length == 0) return;

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile image : images) {
            try {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File destination = new File(UPLOAD_DIR + fileName);
                image.transferTo(destination);
                product.getImages().add(
                        new ProductImage("/uploads/products/" + fileName, product)
                );
            } catch (IOException e) {
                throw new RuntimeException("Image upload failed: " + e.getMessage());
            }
        }
    }

    /* ── Private: map Product entity to response DTO ── */
    private ProductResponseDTO mapToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();

        /* Basic info */
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setBrand(product.getBrand());
        dto.setModel(product.getModel());
        dto.setCondition(product.getCondition());

        /* Category */
        dto.setSubCategory(product.getSubCategory());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getCategoryId());
            dto.setCategoryName(product.getCategory().getCategoryName());
        }

        /* Pricing */
        dto.setPrice(product.getPrice());
        dto.setMrp(product.getMrp());
        dto.setGst(product.getGst());

        /* Inventory */
        dto.setQuantity(product.getQuantity());
        dto.setMinOrderQty(product.getMinOrderQty());
        dto.setMaxOrderQty(product.getMaxOrderQty());

        /* Warranty & origin */
        dto.setWarranty(product.getWarranty());
        dto.setCountryOfOrigin(product.getCountryOfOrigin());

        /* Shipping */
        dto.setWeight(product.getWeight());
        dto.setLength(product.getLength());
        dto.setWidth(product.getWidth());
        dto.setHeight(product.getHeight());
        dto.setFulfilledBy(product.getFulfilledBy());

        /* Specifications & tags */
        dto.setSpecifications(product.getSpecifications());
        dto.setTags(product.getTags());

        /* Status & ownership */
        dto.setActive(product.getActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        if (product.getUserProfile() != null) {
            dto.setUserProfileId(product.getUserProfile().getUserProfileId());
        }

        /* Images */
        List<String> imageUrls = new ArrayList<>();
        if (product.getImages() != null) {
            imageUrls = product.getImages()
                    .stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
        }
        dto.setImages(imageUrls);

        return dto;
    }
    
    @Override
    public List<ScrapyardOrderResponseDTO> getAllScrapyardOrders() {

        return orderRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToScrapyardDTO)
                .collect(Collectors.toList());
    }    
    @Override
    public ScrapyardOrderResponseDTO getOrderById(Long orderId) {

        OrderEntity order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        return mapToScrapyardDTO(order);
    }
    
    @Override
    public ScrapyardOrderResponseDTO updateOrderStatus(
            Long orderId,
            String status
    ) {

        OrderEntity order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setOrderStatus(
                OrderStatus.valueOf(status.toUpperCase())
        );

        OrderEntity updated =
                orderRepository.save(order);

        return mapToScrapyardDTO(updated);
    }
    
    private ScrapyardOrderResponseDTO mapToScrapyardDTO(
            OrderEntity order
    ) {

        ScrapyardOrderResponseDTO dto =
                new ScrapyardOrderResponseDTO();

        dto.setOrderId(order.getOrderId());

        dto.setOrderStatus(
                order.getOrderStatus().name()
        );

        dto.setTotalAmount(
                order.getTotalAmount().doubleValue()
        );

        dto.setPaymentStatus(
                order.getPaymentStatus().name()
        );

        dto.setCreatedAt(order.getCreatedAt());

        UserProfile profile = order.getUserProfile();

        if (profile != null) {

            // BUYER TYPE
            if (
                    profile.getUserRole() != null &&
                    profile.getUserRole().equalsIgnoreCase("COMPANY")
            ) {

                dto.setBuyerType("COMPANY");

                dto.setBuyerName(
                        profile.getCompanyName()
                );

            } else {

                dto.setBuyerType("CUSTOMER");

                dto.setBuyerName(
                        profile.getName()
                );
            }

            // EXTRA DETAILS
            dto.setBuyerPhone(
                    profile.getMobile()
            );

            dto.setBuyerEmail(
                    profile.getEmailId()
            );

            dto.setBuyerAddress(
                    profile.getCompanyAddress()
            );
        }

        List<OrderItemDTO> items =
                order.getItems()
                        .stream()
                        .map(item -> {

                            OrderItemDTO itemDTO =
                                    new OrderItemDTO();

                            itemDTO.setProductId(
                                    item.getProduct().getProductId()
                            );

                            itemDTO.setProductName(
                                    item.getProduct().getProductName()
                            );

                            itemDTO.setQuantity(
                                    item.getQuantity()
                            );

                            itemDTO.setPrice(
                                    item.getPriceAtPurchase()
                            );

                            // PRODUCT IMAGE
                            if (
                                    item.getProduct().getImages() != null &&
                                    !item.getProduct().getImages().isEmpty()
                            ) {

                                itemDTO.setImage(
                                        item.getProduct()
                                                .getImages()
                                                .get(0)
                                                .getImageUrl()
                                );
                            }

                            return itemDTO;

                        })
                        .collect(Collectors.toList());

        dto.setItems(items);

        return dto;
    }
    
//    public void clearOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
//        order.setCleared(true);
//        orderRepository.save(order);
//    }
}