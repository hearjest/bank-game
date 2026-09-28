package com.example.demo.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entities.Account;
import com.example.demo.entities.AccountEntry;
import com.example.demo.entities.Item;
import com.example.demo.entities.Order;
import com.example.demo.enums.ORDER_STATUS;
import com.example.demo.repositories.AccountEntryRepo;
import com.example.demo.repositories.AccountRepo;
import com.example.demo.repositories.ItemRepo;
import com.example.demo.repositories.OrderRepo;
import com.example.demo.enums.ORDER_TYPE;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {
    private final ItemRepo itemRepo;
    private final AccountEntryRepo aer;
    private final AccountRepo accountRepo;
    private final OrderRepo orderRepo;


    public TransactionService(ItemRepo itemRepo,AccountEntryRepo aer,AccountRepo accountRepo,OrderRepo orderRepo)
    {
        this.itemRepo=itemRepo;
        this.aer=aer;
        this.accountRepo=accountRepo;
        this.orderRepo=orderRepo;
    }


    @Transactional
    public boolean attemptOrderTrans(Order order, Account acc){
        try {
            if (order.getClient()!=null){return false;}
            order.setClient(acc);
            completeTransaction(order);

        }catch(RuntimeException error){
            throw error;
        }


        return true;
    }


    @Transactional
    public void completeTransaction(Order order){
        Account buyer;
        Account seller;
        if (order.getType() == ORDER_TYPE.BUY) {
            buyer = order.getPoster();
            seller = order.getClient();
        } else {
            buyer = order.getClient();
            seller = order.getPoster();
        }

        if (buyer.getBalance() < order.getPrice()) {
            throw new RuntimeException("Insufficient Funds to Purchase");
        }

        for (Item i : order.getItems()) {
            i.setOwnerId(buyer.getId());
            itemRepo.save(i);
        }

        Account plAccount = accountRepo.findByUsername("plat");
        if (plAccount == null) {
            throw new RuntimeException("Cannot apply fees/taxes");
        }

        AccountEntry ae = new AccountEntry(Instant.now(), buyer, seller, plAccount, order, order.getPrice(), 0, 0);
        aer.save(ae);

        order.setStatus(ORDER_STATUS.FILLED);
        orderRepo.save(order);
    }


    //TODO: for unique items, create an order with quantity of 1 for each wanted item
    @Transactional //Perspective of seller matching to a buyer who is willing to purchase at their price
    public void sellToExistingBuyOrder(Order o, Account seller, List<Item> items){
        Account buyer = o.getPoster();
        int quantity = items.size();
        if (quantity > o.getRemainingQuantity()) {
            throw new RuntimeException(
                "Buy order only wants " + o.getRemainingQuantity() + " more, was offered " + quantity);
        }

        // the buy order's price is the buyer's maximum and is what they actually pay
        long total = o.getPrice() * quantity;
        if (buyer.getBalance() < total) {
            throw new RuntimeException("Buyer has insufficient funds");
        }

        for (Item item : items) {
            item.setCurrOrder(null);   // release from the seller's listing, if it was in one
            item.setOwnerId(buyer.getId());
            itemRepo.save(item);
        }

        Account plAccount = accountRepo.findByUsername("plat");
        if (plAccount == null) {
            throw new RuntimeException("Cannot apply fees/taxes");
        }

        AccountEntry ae = new AccountEntry(Instant.now(), buyer, seller, plAccount, o, total, 0, 0);
        aer.save(ae);

        // a buy order holds no items to pop, so the fill is tracked on the counter
        o.recordFill(quantity);
        if (o.getRemainingQuantity() == 0) {
            o.setClient(seller);
            o.setCompletedDate();
            o.setStatus(ORDER_STATUS.FILLED);
        } else {
            o.setStatus(ORDER_STATUS.IN_PROGRESS);
        }
        orderRepo.save(o);
    }

    @Transactional //perspective of a buyer matching to a seller within their maximum price
    public void buyExistingSellOrder(Order o, Account buyer, int quantity){
        Account seller=o.getPoster();
        long total = o.getPrice() * quantity;
        if(buyer.getBalance() < total){
            throw new RuntimeException("Insufficient funds");
        }

        // pops only what's being bought, so a partial fill leaves the rest of the listing open
        for (Item item : o.popItems(quantity)) {
            item.setOwnerId(buyer.getId());
            itemRepo.save(item);
        }

        Account plAccount = accountRepo.findByUsername("plat");
        if (plAccount == null) {
            throw new RuntimeException("Cannot apply fees/taxes");
        }

        AccountEntry ae = new AccountEntry(Instant.now(), buyer, seller, plAccount, o, total, 0, 0);
        aer.save(ae);

        if (o.getRemainingQuantity() == 0) {
            o.setClient(buyer);
            o.setCompletedDate();
            o.setStatus(ORDER_STATUS.FILLED);
        } else {
            o.setStatus(ORDER_STATUS.IN_PROGRESS);
        }
        orderRepo.save(o);
    }



    @Transactional 
    public void buyUniqueItemNormally(Order o, Account buyer){
        if(o.getStatus()!=ORDER_STATUS.OPEN){throw new RuntimeException("Oder not available");}

        if(buyer.getBalance()<o.getPrice()){throw new RuntimeException("Insufficient Funds");}

        o.setStatus(ORDER_STATUS.FILLED);
        o.setClient(buyer);
        o.setCompletedDate(Instant.now());


        Item i=o.getItems().get(0);
        i.setOwnerId(buyer.getId());
        itemRepo.save(i);

        AccountEntry ae=new AccountEntry(Instant.now(), buyer, o.getPoster(), accountRepo.findByUsername("plat"), o, o.getRemainingQuantity(), 0, 0);
        aer.save(ae);
    }
}
