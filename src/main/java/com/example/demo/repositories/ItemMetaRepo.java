package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.ItemMeta;

public interface ItemMetaRepo extends JpaRepository<ItemMeta,Long>{
    ItemMeta findByName(String name);
    ItemMeta findByGameId(Long gameId);


}
