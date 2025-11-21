package com.ecom.retailsoftware.service.impl;

import com.ecom.retailsoftware.entity.CategoryEntity;
import com.ecom.retailsoftware.entity.ItemEntity;
import com.ecom.retailsoftware.io.ItemRequest;
import com.ecom.retailsoftware.io.ItemResponse;
import com.ecom.retailsoftware.repository.CategoryRepository;
import com.ecom.retailsoftware.repository.ItemRepository;
import com.ecom.retailsoftware.service.FileUploadService;
import com.ecom.retailsoftware.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final FileUploadService fileUploadService;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemResponse add(ItemRequest request, MultipartFile file) {
        String imgUrl = fileUploadService.uploadFile(file);
        ItemEntity newItem = convertToEntity(request);
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));
        newItem.setCategory(existingCategory);
        newItem.setImageUrl(imgUrl);
        newItem = itemRepository.save(newItem);
        return convertToResponse(newItem);

    }

    public ItemResponse update(ItemRequest request) {
        ItemEntity existing = itemRepository.findByItemId(request.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        // Who is calling?
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean canAdjustInventory = hasAuthority(auth, "INVENTORY_ADJUST");
        boolean canChangePrice     = hasAuthority(auth, "PRICING_UPDATE");
        boolean canEditMeta        = hasAuthority(auth, "ITEMS_WRITE"); // optional for name/desc/category

        // PRICE – block if changed without PRICING_UPDATE
        if (request.getPrice() != null && !request.getPrice().equals(existing.getPrice())) {
            if (!canChangePrice) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to change price");
            }
            existing.setPrice(request.getPrice());
        }

        // STOCK – allow with INVENTORY_ADJUST
        if (request.getStockQuantity() != null && !request.getStockQuantity().equals(existing.getStockQuantity())) {
            if (!canAdjustInventory) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to adjust inventory");
            }
            if (request.getStockQuantity() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stockQuantity must be >= 0");
            }
            existing.setStockQuantity(request.getStockQuantity());
        }

        // LOW-STOCK THRESHOLD – treat as inventory config
        if (request.getLowStockThreshold() != null
                && !request.getLowStockThreshold().equals(existing.getLowStockThreshold())) {
            if (!canAdjustInventory) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to change threshold");
            }
            existing.setLowStockThreshold(request.getLowStockThreshold());
        }

        // OPTIONAL: name/description/category behind ITEMS_WRITE (or MANAGER)
        if (request.getName() != null && !request.getName().isBlank()) {
            if (!canEditMeta) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to edit name");
            existing.setName(request.getName());
        }
        if (request.getDescription() != null) {
            if (!canEditMeta) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to edit description");
            existing.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            if (!canEditMeta) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to change category");
            var category = categoryRepository.findByCategoryId(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
            existing.setCategory(category);
        }

        existing = itemRepository.save(existing);
        return convertToResponse(existing);
    }

    private boolean hasAuthority(org.springframework.security.core.Authentication auth, String code) {
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (ga.getAuthority().equals(code)) return true;
        }
        return false;
    }

    @Override
    public List<ItemResponse> getLowStockItems(Integer threshold) {
        int t = (threshold != null ? threshold : 5);
        return itemRepository.findByStockQuantityLessThanEqualOrderByStockQuantityAsc(t)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<ItemResponse> getOutOfStockItems() {
        return itemRepository.findByStockQuantityOrderByNameAsc(0)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ItemEntity convertToEntity(ItemRequest request) {

        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .lowStockThreshold(request.getLowStockThreshold())
                .stockQuantity(request.getStockQuantity())
                .build();

    }

    private ItemResponse convertToResponse(ItemEntity newItem) {
        return ItemResponse.builder()
                .ItemId(newItem.getItemId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .price(newItem.getPrice())
                .imgUrl(newItem.getImageUrl())
                .categoryName(newItem.getCategory().getName())
                .categoryId(newItem.getCategory().getCategoryId())
                .createdAt(newItem.getCreatedAt())
                .updatedAt(newItem.getUpdatedAt())
                .lowStockThreshold(newItem.getLowStockThreshold())
                .stockQuantity(newItem.getStockQuantity())
                .build();
    }

    @Override
    public List<ItemResponse> fetchAll() {
        return itemRepository.findAll()
                .stream()
                .map(itemEntity -> convertToResponse(itemEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(String itemId) {
        ItemEntity existingItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        Boolean isFileDeleted = fileUploadService.deleteFile(existingItem.getImageUrl());

        if (isFileDeleted) {
            itemRepository.delete(existingItem);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete image");
        }
    }

}
