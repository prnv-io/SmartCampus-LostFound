package com.campus.lostfound.service;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // Register a lost or found item
    public Item registerItem(Item item) {
        item.setReportedTime(LocalDateTime.now());
        item.setStatus("REGISTERED");
        return itemRepository.save(item);
    }

    // Fetch all items (admin/testing)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}

