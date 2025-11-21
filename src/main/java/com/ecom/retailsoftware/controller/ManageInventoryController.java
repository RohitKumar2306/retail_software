package com.ecom.retailsoftware.controller;

import com.ecom.retailsoftware.io.ItemRequest;
import com.ecom.retailsoftware.io.ItemResponse;
import com.ecom.retailsoftware.service.ItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class ManageInventoryController {

    private final ItemService itemService;

    @PreAuthorize("hasAuthority('INVENTORY_ADJUST')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/update/{itemId}")
    public ItemResponse updateItem(@PathVariable String itemId,
                                   @RequestPart("item") String item) {
        ObjectMapper objectMapper = new ObjectMapper();
        ItemRequest itemRequest = null;
        try {
            itemRequest = objectMapper.readValue(item, ItemRequest.class);
            itemRequest.setItemId(itemId);
            return itemService.update(itemRequest);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
