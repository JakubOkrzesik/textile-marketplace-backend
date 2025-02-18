package com.example.textilemarketplacebackend.products.services;

import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.orders.models.DTOs.MessageDTO;
import com.example.textilemarketplacebackend.orders.models.Order;
import com.example.textilemarketplacebackend.products.models.*;
import com.example.textilemarketplacebackend.products.models.DTOs.ProductDTO;
import com.example.textilemarketplacebackend.products.models.DTOs.ProductEnumDTO;
import com.example.textilemarketplacebackend.products.models.DTOs.BuyerSellerDTO;
import com.example.textilemarketplacebackend.products.models.productEnums.*;
import com.example.textilemarketplacebackend.mail.models.MailRequest;
import com.example.textilemarketplacebackend.mail.models.MailRequestType;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import com.example.textilemarketplacebackend.global.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private Logger logger = LoggerFactory.getLogger(ProductService.class);

    public List<ProductDTO> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(listing -> modelMapper.map(listing, ProductDTO.class))
                .collect(Collectors.toList());
    }

    public List<BuyerSellerDTO> getUserProducts(String authHeader) {
        List<BuyerSellerDTO> buyerSellerDTOList = new ArrayList<>();
        User user = userService.extractUserFromToken(authHeader);
        List<ProductListing> userProducts = productRepository.findAllByUser(user)
                .orElse(new ArrayList<>());

        // TODO needs reevaluation need to return list of ProductListing to avoid using double for loops

        for (ProductListing productListing : userProducts) {

            if (productListing.getOrders() != null && !productListing.getOrders().isEmpty()) {
                for (Order order : productListing.getOrders()) {
                    BuyerSellerDTO buyerSellerDTO = BuyerSellerDTO.builder()
                            .listingName(productListing.getProductName())
                            .listingQuantity(productListing.getQuantity())
                            .listingId(productListing.getId())
                            .oldOrderPrice(productListing.getPrice())
                            .messages(order.getMessageList().stream().map(message -> modelMapper.map(message, MessageDTO.class)).toList())
                            .build();

                    buyerSellerDTO.setId(order.getId());
                    buyerSellerDTO.setOrderQuantity(order.getOrderQuantity());
                    buyerSellerDTO.setNewOrderPrice(order.getNewOrderPrice());
                    buyerSellerDTO.setOrderStatus(order.getOrderStatus());
                    buyerSellerDTOList.add(buyerSellerDTO);
                }
            } else {
                BuyerSellerDTO buyerSellerDTO = BuyerSellerDTO.builder()
                        .listingName(productListing.getProductName())
                        .listingQuantity(productListing.getQuantity())
                        .listingId(productListing.getId())
                        .oldOrderPrice(productListing.getPrice())
                        .build();

                buyerSellerDTOList.add(buyerSellerDTO);
            }

            // PROSZE TAK NIE PROGRAMOWAC!!!

        }

        return buyerSellerDTOList;
    }

    public ProductDTO getProductById(Long id, String authHeader) {
        ProductListing productListing = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No offer found with this Id"));

        User user = userService.extractUserFromToken(authHeader);

        ProductDTO productDTO = modelMapper.map(productListing, ProductDTO.class);
        productDTO.setSeller(productListing.getUser().equals(user));

        return productDTO;
    }

    public void postProduct(String authHeader, ProductDTO productDTO) {
        User user = userService.extractUserFromToken(authHeader);
        List<MailRequest> mailRequestList = new ArrayList<>();

        ProductListing productListing = ProductListing.builder()
                .productName(productDTO.getProductName())
                .shortDescription(productDTO.getShortDescription())
                .longDescription(productDTO.getLongDescription())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .images(productDTO.getImages())
                .fabricType(productDTO.getFabricType())
                .composition(productDTO.getComposition())
                .technologies(productDTO.getTechnologies())
                .safetyRequirements(productDTO.getSafetyRequirements())
                .exporters(productDTO.getExporters())
                .originalProductNames(productDTO.getOriginalProductNames())
                .colour(productDTO.getColour())
                .width(productDTO.getWidth())
                .user(user)
                .build();

        MailRequest buyerMailRequest = MailRequest.builder()
                .body(String.format("Your product with the name %s has been successfully posted. You can view your listings by clicking the button below.", productDTO.getProductName()))
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{user.getEmail()})
                .build();
        mailRequestList.add(buyerMailRequest);

        productRepository.save(productListing);
        emailService.sendEmail(mailRequestList);
    }

    public void editProduct(String authHeader, Long id, ProductDTO productDTO) {
        User user = userService.extractUserFromToken(authHeader);

        ProductListing existingProductListing = productRepository.findById(id)
                .filter(offer -> offer.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NoSuchElementException("Offer not found or user is not authorized"));

        existingProductListing.setProductName(productDTO.getProductName());
        existingProductListing.setShortDescription(productDTO.getShortDescription());
        existingProductListing.setLongDescription(productDTO.getLongDescription());
        existingProductListing.setPrice(productDTO.getPrice());
        existingProductListing.setQuantity(productDTO.getQuantity());
        existingProductListing.setImages(productDTO.getImages());
        existingProductListing.setFabricType(productDTO.getFabricType());
        existingProductListing.setComposition(productDTO.getComposition());
        existingProductListing.setTechnologies(productDTO.getTechnologies());
        existingProductListing.setSafetyRequirements(productDTO.getSafetyRequirements());
        existingProductListing.setColour(productDTO.getColour());
        existingProductListing.setWidth(productDTO.getWidth());

        productRepository.save(existingProductListing);
    }

    public void deleteProduct(String authHeader, Long id) {
        User user = userService.extractUserFromToken(authHeader);
        List<MailRequest> mailRequestList = new ArrayList<>();

        ProductListing existingProductListing = productRepository.findById(id)
                .filter(offer -> offer.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));

        MailRequest buyerMailRequest = MailRequest.builder()
                .body(String.format("Your product with the name %s has been successfully deleted.", existingProductListing.getProductName()))
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{user.getEmail()})
                .build();
        mailRequestList.add(buyerMailRequest);

        productRepository.delete(existingProductListing);
        emailService.sendEmail(mailRequestList);
    }

    public ProductEnumDTO getListingEnums() {
        return ProductEnumDTO.builder()
                .fabricTypes(FabricType.values())
                .compositions(FabricComposition.values())
                .technologies(FabricTechnology.values())
                .safetyRequirements(FabricSafetyRequirements.values())
                .originalProductNames(OriginalProductName.values())
                .exporters(ExporterEnum.values())
                .build();
    }
}
