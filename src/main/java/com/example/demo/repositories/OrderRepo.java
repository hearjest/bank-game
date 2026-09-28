package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entities.Account;
import com.example.demo.entities.Order;

import java.util.List;
import com.example.demo.enums.ORDER_TYPE;
import com.example.demo.enums.ORDER_STATUS;

public interface OrderRepo extends JpaRepository<Order,Long>{
    Order findByOrderId(long orderId);
    List<Order> findByPoster(Account poster);
    Order findByClient(Account client);
    List<Order> findByItems_IdAndStatus(long itemId, ORDER_STATUS status);
    List<Order> findByItemMeta_IdAndStatus(long itemMetaId, ORDER_STATUS status);


    @Query("""
    SELECT o FROM Order o
    WHERE o.type = :type
    AND o.status = :status
    AND o.price >= :price
    ORDER BY o.posted ASC""")
    List<Order> findByTypeAndStatusAndPriceGreaterThanEqualOrderByPostedAsc(ORDER_TYPE type, ORDER_STATUS status, long price);

    @Query("""
    SELECT o FROM Order o
    WHERE o.type = :type
    AND o.status = :status
    AND o.price <= :price
    ORDER BY o.posted ASC""")
    List<Order> findByTypeAndStatusAndPriceLessThanEqualOrderByPostedAsc(ORDER_TYPE type, ORDER_STATUS status, long price);


}
