package com.campus.lostfound.service;

import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final RestTemplate restTemplate = new RestTemplate();

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

    // FOUND item → compare with LOST items (ML-based)
if ("FOUND".equalsIgnoreCase(newItem.getItemType())) {

    System.out.println("🔍 Triggering match for FOUND item ID: " + newItem.getItemId());

    List<Item> lostItems = itemRepository.findByItemType("LOST");

    for (Item lostItem : lostItems) {

        if (
            lostItem.getCategory().equalsIgnoreCase(newItem.getCategory()) &&
            lostItem.getZone().equalsIgnoreCase(newItem.getZone())
        ) {
            double score = getSimilarityScore(lostItem, newItem);

            if (score >= 0.7) {
                System.out.println("✅ MATCH FOUND");
                System.out.println("Lost Item ID: " + lostItem.getItemId());
                System.out.println("Found Item ID: " + newItem.getItemId());
                System.out.println("Similarity Score: " + score);
            }
        }
    }
}

}
private double getSimilarityScore(Item lostItem, Item foundItem) {

    String mlUrl = "http://127.0.0.1:8000/match";

    Map<String, Object> requestBody = new HashMap<>();

    Map<String, String> lost = new HashMap<>();
    lost.put("title", lostItem.getTitle());
    lost.put("description", lostItem.getDescription());

    Map<String, String> found = new HashMap<>();
    found.put("title", foundItem.getTitle());
    found.put("description", foundItem.getDescription());

    requestBody.put("lost_item", lost);
    requestBody.put("found_item", found);

    Map response = restTemplate.postForObject(mlUrl, requestBody, Map.class);

    return (double) response.get("similarity_score");
}



}

