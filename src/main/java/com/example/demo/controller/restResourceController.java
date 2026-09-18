package com.example.demo.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
public class restResourceController {
    private static final String template = "Hello, %s";
    private final AtomicLong counter=new AtomicLong();
    private final AccountRepo accountRepo;
    private final OrderRepo orderRepo;
    private final TransactionService transServ;
    private final AccountEntryRepo accountEntryRepo;
    private final ItemRepo itemRepo;
    private final ItemMetaRepo itemMetaRepo;

    public restResourceController(AccountRepo accountRepo, OrderRepo orderRepo, TransactionService transServ, AccountEntryRepo accountEntryRepo,ItemRepo itemRepo, ItemMetaRepo itemMetaRepo) {
        this.accountRepo = accountRepo;
        this.orderRepo=orderRepo;
        this.transServ=transServ;
        this.accountEntryRepo=accountEntryRepo;
        this.itemRepo=itemRepo;
        this.itemMetaRepo=itemMetaRepo;
    }

    // @GetMapping("/api")
    // public restTemp get(@RequestParam String param) {
    //     return new restTemp(counter.incrementAndGet(),template.formatted(param));
    // }

    
    

    
    @GetMapping("/users/getBalance")
    public Long getUserBalance(@RequestParam Long id) {
        return accountEntryRepo.getBalanceOfAcc(accountRepo.findById(id).orElse(null));
    }
    

    @GetMapping("/items/getAllByUserId")
    public List<Item> getItemByUserId(@RequestParam Long id) {
        return itemRepo.findByOwnerId(id);
    }

    @GetMapping("/items/getByUID")
    public List<Item> getItemByUID(@RequestParam Long id) {
        ItemMeta itemMeta = itemMetaRepo.findById(id).orElse(null);
        if (itemMeta == null) {
            return List.of();
        }

        return itemRepo.findByItemSeries(itemMeta);
    }
    

    @GetMapping("/items/getAllByGameID")
    public List<Item> getAllByGameID(@RequestParam Long id) {
        return itemRepo.findByItemSeries_GameId(id);
    }
    


    // @GetMapping("/items/getByGameId")
    // public List<Item> getMethodName(@RequestParam Long id) {
    //     return new String();
    // }
    
    


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


