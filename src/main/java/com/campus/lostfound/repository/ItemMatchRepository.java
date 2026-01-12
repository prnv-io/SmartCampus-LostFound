package com.campus.lostfound.repository;

import com.campus.lostfound.model.ItemMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemMatchRepository extends JpaRepository<ItemMatch, Long> {

    List<ItemMatch> findByLostItem_ItemId(Long lostItemId);

    List<ItemMatch> findByFoundItem_ItemId(Long foundItemId);
}

