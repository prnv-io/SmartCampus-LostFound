package com.campus.lostfound.controller;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // API to register LOST or FOUND item
    @PostMapping
    public Item registerItem(@RequestBody Item item) {
        return itemService.registerItem(item);
    }

    // API to fetch all items (testing/admin)
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }
}

