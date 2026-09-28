package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Item;
import com.example.demo.entities.ItemMeta;

import java.util.List;


public interface ItemRepo extends JpaRepository<Item,Long>{
    List<Item> findByOwnerId(long ownerId);
    List<Item> findByItemSeries(ItemMeta itemSeries);
    List<Item> findByItemSeries_GameId(Long gameId);

    List<Item> findByOwnerIdAndItemSeries(long ownerId, ItemMeta itemSeries);

    //if currOrder is null, it means the item isn't in an open listing (not in escrow)
    List<Item> findByOwnerIdAndItemSeriesAndCurrOrderIsNull(long ownerId, ItemMeta itemSeries);

    Item findByOwnerIdAndId(long ownerId, long id);
    

    
    
}
