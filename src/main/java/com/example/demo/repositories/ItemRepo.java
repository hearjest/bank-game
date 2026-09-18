package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Item;
import com.example.demo.entities.ItemMeta;

import java.util.List;
import java.util.Optional;


public interface ItemRepo extends JpaRepository<Item,Long>{
    List<Item> findByOwnerId(long ownerId);
    List<Item> findByItemSeries(ItemMeta itemSeries);
    List<Item> findByItemSeries_GameId(Long gameId);

    List<Item> findByOwnerIdAndItemSeries(long ownerId, ItemMeta itemSeries);

    List<Item> findByOwnerIdAndItemId(long ownerId, long itemId);
    

    
    
}
