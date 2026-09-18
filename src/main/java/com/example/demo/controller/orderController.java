package com.example.demo.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Account;
import com.example.demo.entities.Item;
import com.example.demo.entities.ItemMeta;
import com.example.demo.entities.Order;
import com.example.demo.repositories.AccountEntryRepo;
import com.example.demo.repositories.AccountRepo;
import com.example.demo.repositories.ItemMetaRepo;
import com.example.demo.repositories.ItemRepo;
import com.example.demo.repositories.OrderRepo;
import com.example.demo.service.TransactionService;
import com.example.demo.enums.ORDER_TYPE;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;


public class orderController{
    private final AccountRepo accountRepo;
    private final OrderRepo orderRepo;
    private final TransactionService transServ;
    private final AccountEntryRepo accountEntryRepo;
    private final ItemRepo itemRepo;
    private final ItemMetaRepo itemMetaRepo;

    public orderController(AccountRepo accountRepo, OrderRepo orderRepo, TransactionService transServ, AccountEntryRepo accountEntryRepo,ItemRepo itemRepo, ItemMetaRepo itemMetaRepo) {
        this.accountRepo = accountRepo;
        this.orderRepo=orderRepo;
        this.transServ=transServ;
        this.accountEntryRepo=accountEntryRepo;
        this.itemRepo=itemRepo;
        this.itemMetaRepo=itemMetaRepo;
    }
    @PostMapping("/order/post")
    public String postOrder(@RequestBody CreateOrderRequest req) {
        Account poster = accountRepo.findById(req.posterId).orElse(null);
        if (poster == null) {
            return "No account found for posterId " + req.posterId;
        }

        Item item = itemRepo.findById(req.itemId).orElse(null);
        if (item == null) {
            return "No item found for itemId " + req.itemId;
        }

        //TODO: auth
        //TODO: verify ok order
        Order order = new Order(poster, item, ORDER_TYPE.valueOf(req.type), (int) req.price,item.getItemSeries());
        orderRepo.save(order);

        return "OK";
    }

    @GetMapping("/order/getOrdersByUser")
    public List<Order> getOrdersByUserId(@RequestParam Long senderId) throws RuntimeException{
        if (senderId==null){throw new RuntimeException("Invalid user");}
        Account acc = accountRepo.findById(senderId).orElse(null);
        if (acc==null){throw new RuntimeException("Invalid user");}
        List<Order> res = orderRepo.findByPoster(acc); 
        return res;
    }

    @GetMapping("/orders/getAll")
    public List<Order> getAllOrders(@RequestParam String param) {
        return orderRepo.findAll();

    }

    @GetMapping("/orders/getByItemId")
    public List<OrderResponse> getOrderByItemId(@RequestParam long id) {
        return orderRepo.findByItem_ItemSeries_IdAndCompletedFalse(id).stream()
                .map(OrderResponse::from)
                .toList();
    }
    

    @PostMapping("/orders/transaction")
    public String attemptTransaction(@RequestBody AttemptOrderRequest req) {
        Order order = orderRepo.findById(req.orderId).orElse(null);
        if (order == null) {
            return "No order found for orderId " + req.orderId;
        }

        Account acc = accountRepo.findById(req.accountId).orElse(null);
        if (acc == null) {
            return "No account found for accountId " + req.accountId;
        }

        try{
            transServ.attemptOrderTrans(order, acc);
        }catch(RuntimeException error){
            return "Transaction failed: "+error.getMessage();
        }

        return "Transaction Succeeded";
    }
    
    @PostMapping("orders/postSellOrder")
    public String postSellOrder(@RequestBody String entity) {
        //TODO: process POST request
        




        return entity;
    }

    public static class CreateOrderRequest{
        public Long posterId;
        public Long itemId;
        public String type;
        public long price;
    }

    public static class AttemptOrderRequest{
        public Long orderId;
        public Long accountId;
    }

    public static class OrderResponse{
        public long orderId;
        public Long posterId;
        public Long clientId;
        public Long itemId;
        public Long itemMetaId;
        public String name;
        public String desc;
        public long price;
        public String type;
        public boolean completed;

        public static OrderResponse from(Order o) {
            OrderResponse r = new OrderResponse();
            r.orderId = o.getOrderId();
            r.posterId = o.getPoster() != null ? o.getPoster().getId() : null;
            r.clientId = o.getClient() != null ? o.getClient().getId() : null;
            Item item = o.getItem();
            if (item != null) {
                r.itemId = item.getId();
                ItemMeta itemSeries = item.getItemSeries();
                if (itemSeries != null) {
                    r.itemMetaId = itemSeries.getId();
                    r.name = itemSeries.getName();
                    r.desc = itemSeries.getDesc();
                }
            }
            r.price = o.getPrice();
            r.type = o.getType().name();
            r.completed = o.isCompleted();
            return r;
        }

}
}
