package com.example.demo.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Account;
import com.example.demo.entities.AccountEntry;
import com.example.demo.entities.Item;
import com.example.demo.entities.Order;
import com.example.demo.repositories.AccountEntryRepo;
import com.example.demo.repositories.AccountRepo;
import com.example.demo.repositories.ItemRepo;
import com.example.demo.repositories.OrderRepo;

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
        if (order.getType() == Order.ORDER_TYPE.BUY) {
            buyer = order.getPoster();
            seller = order.getClient();
        } else {
            buyer = order.getClient();
            seller = order.getPoster();
        }

        if (buyer.getBalance() < order.getPrice()) {
            throw new RuntimeException("Insufficient Funds to Purchase");
        }

        Item i = order.getItem();
        i.setOwnerId(buyer.getId());
        itemRepo.save(i);

        Account plAccount = accountRepo.findByUsername("plat");
        if (plAccount == null) {
            throw new RuntimeException("Cannot apply fees/taxes");
        }

        AccountEntry ae = new AccountEntry(Instant.now(), buyer, seller, plAccount, order, order.getPrice(), 0, 0);
        aer.save(ae);

        order.setCompleted(true);
        orderRepo.save(order);
    }
}
