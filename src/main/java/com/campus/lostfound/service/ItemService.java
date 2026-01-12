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

    Item savedItem = itemRepository.save(item);

    //  Automatically trigger matching
    triggerMatching(savedItem);

    return savedItem;
}


    // Fetch all items (admin/testing)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    public Item updateItemStatus(Long itemId, String newStatus) {
    Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));

    item.setStatus(newStatus);
    return itemRepository.save(item);
}
private void triggerMatching(Item newItem) {

    // LOST item → compare with FOUND items
    if ("LOST".equalsIgnoreCase(newItem.getItemType())) {

        System.out.println("🔍 Triggering match for LOST item ID: " + newItem.getItemId());

        // Fetch FOUND items with same category & zone
        var foundItems = itemRepository.findByItemType("FOUND");

        foundItems.stream()
                .filter(item ->
                        item.getCategory().equalsIgnoreCase(newItem.getCategory()) &&
                        item.getZone().equalsIgnoreCase(newItem.getZone())
                )
                .forEach(item ->
                        System.out.println("👉 Potential FOUND match: ID " + item.getItemId())
                );
    }

    // FOUND item → compare with LOST items
    if ("FOUND".equalsIgnoreCase(newItem.getItemType())) {

        System.out.println("🔍 Triggering match for FOUND item ID: " + newItem.getItemId());

        var lostItems = itemRepository.findByItemType("LOST");

        lostItems.stream()
                .filter(item ->
                        item.getCategory().equalsIgnoreCase(newItem.getCategory()) &&
                        item.getZone().equalsIgnoreCase(newItem.getZone())
                )
                .forEach(item ->
                        System.out.println("👉 Potential LOST match: ID " + item.getItemId())
                );
    }
}


}

