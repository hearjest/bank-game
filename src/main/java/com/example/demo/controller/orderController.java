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
import com.example.demo.enums.ORDER_STATUS;
import com.example.demo.enums.ORDER_TYPE;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
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
        Order order = new Order(poster, List.of(item), ORDER_TYPE.valueOf(req.type), (int) req.price,item.getItemSeries());
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
        return orderRepo.findByItemMeta_IdAndStatus(id, ORDER_STATUS.OPEN).stream()
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
    public String postSellOrder(@RequestBody CreateOrderRequest req) {
        //TODO: process POST request
        //post sell order, therefore, see if there is a buyer with an order
        // with a maximum price greater than or equal to sell price and fulfill it
        Account seller=accountRepo.findById(req.posterId).orElse(null);
        if(seller==null){return "Failed to find poster";}
        ItemMeta meta=itemMetaRepo.findById(req.itemMetaId).orElse(null);
        if(meta==null){return "No item type found for itemMetaId "+req.itemMetaId;}

        List<Item> toSell=itemRepo.findByOwnerIdAndItemSeriesAndCurrOrderIsNull(req.posterId,meta);
        if(toSell.size()<req.quantity){return "Seller does not own enough unlisted copies of that item";}
        toSell=new ArrayList<>(toSell.subList(0,req.quantity));


        List<Order> candidates=orderRepo.findByTypeAndStatusAndPriceGreaterThanEqualOrderByPostedAsc(ORDER_TYPE.BUY, ORDER_STATUS.OPEN, req.price);
        for(Order c:candidates){
            if(toSell.isEmpty()){break;}
            int fill=Math.min(toSell.size(),c.getRemainingQuantity());
            if(fill<=0){continue;}
            // charged at the buy order's price, so that's what has to be affordable
            if(c.getPoster().getBalance()<c.getPrice()*(long)fill){continue;}
            transServ.sellToExistingBuyOrder(c, seller, new ArrayList<>(toSell.subList(0,fill)));
            toSell.subList(0,fill).clear();
        }
        if(toSell.isEmpty()){return "Sold";}

        //whatever didn't match goes up as an open listing and  escrow the leftover items
        Order o=new Order(seller,toSell,ORDER_TYPE.SELL,(int)req.price,meta);
        orderRepo.save(o);
        return "Posted";

    }


    @PostMapping("orders/postBuyOrder")
    public String postBuyOrder(@RequestBody CreateOrderRequest req){
        //post buy order, see if there is seller whose price is less than or equal your maximum price
        Account buyer=accountRepo.findById(req.posterId).orElse(null);
        if(buyer==null){return "Failed to find poster";}
        long balance=buyer.getBalance();

        List<Order> candidates=orderRepo.findByTypeAndStatusAndPriceLessThanEqualOrderByPostedAsc(ORDER_TYPE.SELL, ORDER_STATUS.OPEN, req.price);
        int remain=req.quantity;
        for(Order c:candidates){
            int fill=Math.min(remain,c.getRemainingQuantity());
            if(fill>0 && balance>=c.getPrice()*(long)fill){
                transServ.buyExistingSellOrder(c,buyer,fill);
                balance-=c.getPrice()*(long)fill;
                remain-=fill;
                if(remain==0){return "Bought";}
            }
        }

        // nothing (or not enough) matched - post the remainder as an open buy order.
        // A buy order holds no items: the buyer doesn't own any yet.
        ItemMeta im=itemMetaRepo.findById(req.itemMetaId).orElse(null);
        if(im==null){return "No item type found for itemMetaId "+req.itemMetaId;}
        Order o=new Order(buyer,null,ORDER_TYPE.BUY,(int)req.price,im,remain);
        orderRepo.save(o);
        return "Posted";
    }

    public static class CreateOrderRequest{
        public Long posterId;
        public Long itemId;
        public Long itemMetaId;
        public String type;
        public long price;
        public int quantity;
    }

    public static class AttemptOrderRequest{
        public Long orderId;
        public Long accountId;
    }

    public static class OrderResponse{
        public long orderId;
        public Long posterId;
        public Long clientId;
        public Long itemMetaId;
        public String name;
        public String desc;
        public int quantity;
        public long price;
        public String type;
        public ORDER_STATUS status;

        public static OrderResponse from(Order o) {
            OrderResponse r = new OrderResponse();
            r.orderId = o.getOrderId();
            r.posterId = o.getPoster() != null ? o.getPoster().getId() : null;
            r.clientId = o.getClient() != null ? o.getClient().getId() : null;
            ItemMeta itemMeta = o.getItemMeta();
            if (itemMeta != null) {
                r.itemMetaId = itemMeta.getId();
                r.name = itemMeta.getName();
                r.desc = itemMeta.getDesc();
            }
            r.quantity = o.getRemainingQuantity();
            r.price = o.getPrice();
            r.type = o.getType().name();
            r.status = o.getStatus();
            return r;
        }

}
}
