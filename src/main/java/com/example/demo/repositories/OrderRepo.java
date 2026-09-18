package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Account;
import com.example.demo.entities.Order;

import java.util.List;


public interface OrderRepo extends JpaRepository<Order,Long>{
    Order findByOrderId(long orderId);
    List<Order> findByPoster(Account poster);
    Order findByClient(Account client);
    List<Order> findByItem_IdAndCompletedFalse(long itemId);
    List<Order> findByItem_ItemSeries_IdAndCompletedFalse(long itemMetaId);

}
