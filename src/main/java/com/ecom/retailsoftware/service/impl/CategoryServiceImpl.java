package com.ecom.retailsoftware.service.impl;

import com.ecom.retailsoftware.entity.CategoryEntity;
import com.ecom.retailsoftware.io.CategoryRequest;
import com.ecom.retailsoftware.io.CategoryResponse;
import com.ecom.retailsoftware.repository.CategoryRepository;
import com.ecom.retailsoftware.service.CategoryService;
import com.ecom.retailsoftware.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    private final FileUploadService fileUploadService;

    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile file) {

        String imageUrl = fileUploadService.uploadFile(file);
        CategoryEntity newCategory = convertToEntity(request);
        newCategory.setImageUrl(imageUrl);
        newCategory = categoryRepository.save(newCategory);
        return convertToResponse(newCategory);
    }

    @Override
    public List<CategoryResponse> read() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryEntity -> convertToResponse(categoryEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategoryEntity = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: "+categoryId));

        fileUploadService.deleteFile(existingCategoryEntity.getImageUrl());
        categoryRepository.delete(existingCategoryEntity);
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {

        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .imageUrl(newCategory.getImageUrl())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {

        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();
    }
}
