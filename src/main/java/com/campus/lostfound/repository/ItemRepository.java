package com.campus.lostfound.repository;

import com.campus.lostfound.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemType(String itemType);

    List<Item> findByCategoryIgnoreCase(String category);

    List<Item> findByZoneIgnoreCase(String zone);

    List<Item> findByStatus(String status);
}

