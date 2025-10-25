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

    @Override
    public ItemResponse update(ItemRequest request) {
        // 1) Find existing item
        ItemEntity existing = itemRepository.findByItemId(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found: " + request.getItemId()));

        // 2) Apply updates (only when provided)
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            existing.setPrice(request.getPrice());
        }
        if (request.getLowStockThreshold() != null) {
            existing.setLowStockThreshold(request.getLowStockThreshold());
        }
        if (request.getStockQuantity() != null) {
            if (request.getStockQuantity() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stockQuantity must be >= 0");
            }
            existing.setStockQuantity(request.getStockQuantity());
        }
        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findByCategoryId(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));
            existing.setCategory(category);
        }

        // 3) Persist
        existing = itemRepository.save(existing);

        // 4) Map to response (your existing helper)
        return convertToResponse(existing);
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
