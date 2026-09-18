package com.example.demo.entities;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ItemMetaCache {

    private final Map<Long, ItemMeta> cache = new ConcurrentHashMap<>();

    public void loadAll(List<ItemMeta> all) {
        for (ItemMeta meta : all) {
            cache.put(meta.getId(), meta);
        }
    }

    public ItemMeta get(Long id) {
        return cache.get(id);
    }
}