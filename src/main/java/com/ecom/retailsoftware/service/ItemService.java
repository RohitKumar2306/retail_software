package com.ecom.retailsoftware.service;

import com.ecom.retailsoftware.io.ItemRequest;
import com.ecom.retailsoftware.io.ItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {

    ItemResponse add(ItemRequest request, MultipartFile file);

    List<ItemResponse> fetchAll();

    void deleteItem(String itemId);

    ItemResponse update(ItemRequest request);

    List<ItemResponse> getLowStockItems(Integer threshold);

    List<ItemResponse> getOutOfStockItems();
}
