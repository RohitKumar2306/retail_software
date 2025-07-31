package com.ecom.retailsoftware.service;

import com.ecom.retailsoftware.io.CategoryRequest;
import com.ecom.retailsoftware.io.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {

    CategoryResponse add(CategoryRequest request, MultipartFile file);

    List<CategoryResponse> read();

    void delete(String categoryId);
}
